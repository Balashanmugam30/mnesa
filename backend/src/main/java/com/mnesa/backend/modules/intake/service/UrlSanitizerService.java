package com.mnesa.backend.modules.intake.service;

import com.mnesa.backend.common.exception.MnesaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Enterprise URL sanitizer providing SSRF defense, tracking parameter stripping,
 * and canonicalization for shared intake content.
 */
@Service
public class UrlSanitizerService {

    private static final Logger log = LoggerFactory.getLogger(UrlSanitizerService.class);

    // Regex finding standard http/https URLs in arbitrary text
    private static final Pattern URL_PATTERN = Pattern.compile(
            "https?://[a-zA-Z0-9\\-\\._~:/?#\\[\\]@!$&'()*+,;=%]+",
            Pattern.CASE_INSENSITIVE
    );

    // Common marketing / telemetry tracking parameters to strip
    private static final Set<String> TRACKING_PARAMS = Set.of(
            "utm_source", "utm_medium", "utm_campaign", "utm_term", "utm_content", "utm_id",
            "fbclid", "gclid", "msclkid", "dclid", "twclid", "igshid", "si", "trk",
            "ref", "ref_src", "ref_url", "feature", "mc_cid", "mc_eid"
    );

    /**
     * Extracts the first valid URL found within the text or null if none.
     */
    public String extractFirstUrl(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }
        Matcher matcher = URL_PATTERN.matcher(text);
        if (matcher.find()) {
            String url = matcher.group();
            while (url.endsWith("!") || url.endsWith(".") || url.endsWith(",") ||
                   url.endsWith("?") || url.endsWith(";") || url.endsWith(")") || url.endsWith("]")) {
                url = url.substring(0, url.length() - 1);
            }
            return url;
        }
        return null;
    }

    /**
     * Validates and normalizes an untrusted URL.
     * Enforces SSRF defense: rejects private/reserved IP blocks, link-local, and loopback.
     */
    public String sanitizeAndNormalize(String rawUrl) {
        if (rawUrl == null || rawUrl.isBlank()) {
            return null;
        }

        URI uri;
        try {
            uri = URI.create(rawUrl.trim()).normalize();
        } catch (Exception e) {
            throw new MnesaException("Malformed or unparseable URL provided", HttpStatus.BAD_REQUEST, "INVALID_URL");
        }

        String scheme = uri.getScheme();
        if (scheme == null || (!scheme.equalsIgnoreCase("http") && !scheme.equalsIgnoreCase("https"))) {
            throw new MnesaException("URL must use HTTP or HTTPS protocol", HttpStatus.BAD_REQUEST, "UNSUPPORTED_PROTOCOL");
        }

        String host = uri.getHost();
        if (host == null || host.isBlank()) {
            throw new MnesaException("URL is missing a valid host", HttpStatus.BAD_REQUEST, "INVALID_URL_HOST");
        }

        // SSRF Check: Validate host IP
        validateHostSsrf(host);

        // Strip tracking parameters from query string
        String normalizedQuery = sanitizeQuery(uri.getRawQuery());

        try {
            // Normalize path: default to "" if null, strip redundant trailing slashes if only "/"
            String path = uri.getRawPath() == null ? "" : uri.getRawPath();
            if (path.length() > 1 && path.endsWith("/")) {
                path = path.substring(0, path.length() - 1);
            }

            int port = uri.getPort();
            // Omit standard ports
            if ((scheme.equalsIgnoreCase("http") && port == 80) ||
                (scheme.equalsIgnoreCase("https") && port == 443)) {
                port = -1;
            }

            URI canonicalUri = new URI(
                    scheme.toLowerCase(Locale.ROOT),
                    uri.getUserInfo(), // will be checked or stripped
                    host.toLowerCase(Locale.ROOT),
                    port,
                    path.isEmpty() ? null : path,
                    normalizedQuery,
                    null // Strip fragments / anchors
            );

            return canonicalUri.toASCIIString();
        } catch (Exception e) {
            log.warn("Failed to reconstruct normalized URI: {}", e.getMessage());
            return rawUrl.trim();
        }
    }

    /**
     * Extracts clean domain name for source tagging (e.g., linkedin.com).
     */
    public String extractDomain(String rawUrl) {
        if (rawUrl == null || rawUrl.isBlank()) {
            return null;
        }
        try {
            URI uri = URI.create(rawUrl.trim());
            String host = uri.getHost();
            if (host == null) {
                return null;
            }
            host = host.toLowerCase(Locale.ROOT);
            if (host.startsWith("www.")) {
                host = host.substring(4);
            }
            return host;
        } catch (Exception e) {
            return null;
        }
    }

    private void validateHostSsrf(String host) {
        String cleanHost = host.toLowerCase(Locale.ROOT);

        // Reject obvious loopback and metadata hostnames
        if (cleanHost.equals("localhost") ||
            cleanHost.endsWith(".localhost") ||
            cleanHost.endsWith(".local") ||
            cleanHost.endsWith(".internal") ||
            cleanHost.contains("169.254.169.254") ||
            cleanHost.equals("metadata.google.internal")) {
            throw new MnesaException("Destination host is forbidden by security policy", HttpStatus.BAD_REQUEST, "SSRF_FORBIDDEN_HOST");
        }

        // DNS resolution check to detect private/reserved IP addresses
        try {
            InetAddress[] addresses = InetAddress.getAllByName(cleanHost);
            for (InetAddress addr : addresses) {
                if (addr.isLoopbackAddress() ||
                    addr.isSiteLocalAddress() ||
                    addr.isLinkLocalAddress() ||
                    addr.isAnyLocalAddress() ||
                    addr.isMulticastAddress()) {
                    throw new MnesaException("Host resolves to a restricted internal network address", HttpStatus.BAD_REQUEST, "SSRF_RESTRICTED_IP");
                }

                byte[] bytes = addr.getAddress();
                if (bytes.length == 4) {
                    // Check IPv4 169.254.0.0/16 (Link Local / Cloud Metadata)
                    int b0 = bytes[0] & 0xFF;
                    int b1 = bytes[1] & 0xFF;
                    if (b0 == 169 && b1 == 254) {
                        throw new MnesaException("Cloud metadata access is strictly forbidden", HttpStatus.BAD_REQUEST, "SSRF_METADATA_FORBIDDEN");
                    }
                    // Check 0.0.0.0/8
                    if (b0 == 0) {
                        throw new MnesaException("Zero address range is forbidden", HttpStatus.BAD_REQUEST, "SSRF_RESTRICTED_IP");
                    }
                }
            }
        } catch (MnesaException me) {
            throw me;
        } catch (Exception e) {
            // If DNS resolution fails, log and allow if valid hostname syntax
            log.debug("DNS lookup did not resolve host [{}]: {}", cleanHost, e.getMessage());
        }
    }

    private String sanitizeQuery(String rawQuery) {
        if (rawQuery == null || rawQuery.isBlank()) {
            return null;
        }

        String[] pairs = rawQuery.split("&");
        List<String> cleanPairs = new ArrayList<>();

        for (String pair : pairs) {
            if (pair.isBlank()) continue;
            int eqIdx = pair.indexOf('=');
            String paramName = eqIdx > 0 ? pair.substring(0, eqIdx) : pair;
            try {
                String decodedName = URLDecoder.decode(paramName, StandardCharsets.UTF_8).toLowerCase(Locale.ROOT);
                if (!TRACKING_PARAMS.contains(decodedName)) {
                    cleanPairs.add(pair);
                }
            } catch (Exception e) {
                cleanPairs.add(pair);
            }
        }

        return cleanPairs.isEmpty() ? null : String.join("&", cleanPairs);
    }
}
