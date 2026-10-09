import asyncio
import ipaddress
import socket
from typing import List, Optional, Set, Tuple
from urllib.parse import urljoin, urlparse

import httpx
from app.core.logging import logger

BLOCKED_HOSTNAMES: Set[str] = {
    "localhost",
    "metadata.google.internal",
    "metadata.aws.internal",
    "instance-data",
    "postgres",
    "valkey",
    "minio",
    "backend",
    "host.docker.internal",
    "gateway.docker.internal",
}

BLOCKED_IP_NETWORKS = [
    ipaddress.ip_network("0.0.0.0/8"),          # Current network
    ipaddress.ip_network("10.0.0.0/8"),         # RFC 1918 Private
    ipaddress.ip_network("100.64.0.0/10"),      # Shared Address Space (CGNAT)
    ipaddress.ip_network("127.0.0.0/8"),        # Loopback
    ipaddress.ip_network("169.254.0.0/16"),     # Link Local / Cloud Metadata
    ipaddress.ip_network("172.16.0.0/12"),      # RFC 1918 Private
    ipaddress.ip_network("192.0.0.0/24"),       # IETF Protocol Assignments
    ipaddress.ip_network("192.0.2.0/24"),       # TEST-NET-1
    ipaddress.ip_network("192.168.0.0/16"),     # RFC 1918 Private
    ipaddress.ip_network("198.18.0.0/15"),      # Benchmarking
    ipaddress.ip_network("198.51.100.0/24"),    # TEST-NET-2
    ipaddress.ip_network("203.0.113.0/24"),     # TEST-NET-3
    ipaddress.ip_network("224.0.0.0/4"),        # Multicast
    ipaddress.ip_network("240.0.0.0/4"),        # Reserved
    ipaddress.ip_network("255.255.255.255/32"), # Broadcast
    # IPv6 Blocked Networks
    ipaddress.ip_network("::1/128"),            # Loopback
    ipaddress.ip_network("::/128"),             # Unspecified
    ipaddress.ip_network("::ffff:0:0/96"),      # IPv4-mapped IPv6
    ipaddress.ip_network("100::/64"),           # Discard-Only
    ipaddress.ip_network("2001:db8::/32"),      # Documentation
    ipaddress.ip_network("fc00::/7"),           # Unique Local Address (ULA)
    ipaddress.ip_network("fe80::/10"),          # Link-local Unicast
    ipaddress.ip_network("ff00::/8"),           # Multicast
]

MAX_REDIRECTS = 3
MAX_RESPONSE_BYTES = 2 * 1024 * 1024  # 2 MB
DEFAULT_TIMEOUT_SECONDS = 10.0

ALLOWED_CONTENT_TYPES = [
    "text/html",
    "application/xhtml+xml",
    "text/plain",
    "application/json",
]


class FetchError(Exception):
    """Base exception for safe web fetching."""
    pass


class SsrfViolationError(FetchError):
    """Raised when URL violates SSRF protection boundaries."""
    pass


class ContentTooLargeError(FetchError):
    """Raised when response exceeds maximum allowed size."""
    pass


class UnsupportedContentTypeError(FetchError):
    """Raised when server returns unsupported content type (e.g. binary/executable)."""
    pass


class FetchTimeoutError(FetchError):
    """Raised when request exceeds processing deadline."""
    pass


class ContentUnavailableError(FetchError):
    """Raised when remote page returns 4xx/5xx or connection error."""
    pass


def is_ip_blocked(ip: ipaddress.IPv4Address | ipaddress.IPv6Address) -> bool:
    """Checks whether an IP address belongs to any private or reserved network."""
    # Check for IPv4-mapped IPv6
    if isinstance(ip, ipaddress.IPv6Address) and ip.ipv4_mapped:
        ip = ip.ipv4_mapped

    for network in BLOCKED_IP_NETWORKS:
        if ip in network:
            return True
    return False


def validate_url_and_ip(url_str: str) -> Tuple[str, List[ipaddress.IPv4Address | ipaddress.IPv6Address]]:
    """
    Deterministically validates a URL scheme, hostname, and resolves its IPs,
    enforcing zero-trust SSRF protections before any socket connection is opened.
    """
    if not url_str or not isinstance(url_str, str):
        raise SsrfViolationError("URL cannot be empty")

    parsed = urlparse(url_str.strip())

    # 1. Enforce scheme: HTTP/HTTPS only
    scheme = parsed.scheme.lower()
    if scheme not in ("http", "https"):
        raise SsrfViolationError(f"Prohibited scheme: '{scheme}'. Only HTTP and HTTPS are permitted.")

    # 2. Hostname validation
    host = parsed.hostname
    if not host:
        raise SsrfViolationError("Invalid URL: Missing host")

    host_lower = host.lower().strip(".")

    # Blocked known hostnames
    if host_lower in BLOCKED_HOSTNAMES:
        raise SsrfViolationError(f"Host '{host}' is in the prohibited internal service blocklist")

    # Check for literal IP address in host
    try:
        ip_obj = ipaddress.ip_address(host_lower)
        if is_ip_blocked(ip_obj):
            raise SsrfViolationError(f"Host IP '{host}' belongs to a private/reserved address space")
        return host_lower, [ip_obj]
    except ValueError:
        # Not a literal IP; proceed to DNS resolution
        pass

    # 3. DNS resolution and address validation
    try:
        addr_info = socket.getaddrinfo(host_lower, parsed.port or (443 if scheme == "https" else 80), socket.AF_UNSPEC, socket.SOCK_STREAM)
    except socket.gaierror as e:
        raise ContentUnavailableError(f"DNS resolution failed for '{host}': {str(e)}")

    resolved_ips: List[ipaddress.IPv4Address | ipaddress.IPv6Address] = []
    for entry in addr_info:
        sockaddr = entry[4]
        ip_str = sockaddr[0]
        try:
            ip_obj = ipaddress.ip_address(ip_str)
            if is_ip_blocked(ip_obj):
                raise SsrfViolationError(f"Resolved destination '{host}' -> '{ip_str}' is inside a blocked network")
            resolved_ips.append(ip_obj)
        except ValueError:
            raise SsrfViolationError(f"Unparseable resolved IP '{ip_str}' for host '{host}'")

    if not resolved_ips:
        raise ContentUnavailableError(f"No valid IP addresses resolved for host '{host}'")

    return host_lower, resolved_ips


class SafeWebFetcher:
    """
    Production-grade SSRF-safe HTTP client with manual redirect verification,
    response-size streaming caps, and content-type enforcement.
    """

    def __init__(self,
                 timeout_seconds: float = DEFAULT_TIMEOUT_SECONDS,
                 max_redirects: int = MAX_REDIRECTS,
                 max_bytes: int = MAX_RESPONSE_BYTES):
        self.timeout_seconds = timeout_seconds
        self.max_redirects = max_redirects
        self.max_bytes = max_bytes

    async def fetch(self, url: str) -> Tuple[str, str, str]:
        """
        Safely fetches content from public URL.

        Returns:
            Tuple of (html_or_text_content, content_type, final_url)
        """
        current_url = url.strip()
        redirect_count = 0

        async with httpx.AsyncClient(
            follow_redirects=False,
            timeout=httpx.Timeout(self.timeout_seconds),
            headers={
                "User-Agent": "MNESA-Bot/1.0 (+https://mnesa.ai; Opportunity Capture Engine)",
                "Accept": "text/html,application/xhtml+xml,text/plain;q=0.9,*/*;q=0.8",
                "Accept-Language": "en-US,en;q=0.9",
            }
        ) as client:
            while True:
                # 1. Enforce SSRF validation on current URL before connection
                validate_url_and_ip(current_url)

                try:
                    # Stream response to enforce hard byte limit
                    async with client.stream("GET", current_url) as response:
                        # 2. Check for redirect responses (301, 302, 303, 307, 308)
                        if response.status_code in (301, 302, 303, 307, 308):
                            location = response.headers.get("location")
                            if not location:
                                raise ContentUnavailableError(f"Redirect response {response.status_code} missing Location header")

                            redirect_count += 1
                            if redirect_count > self.max_redirects:
                                raise SsrfViolationError(f"Exceeded maximum allowed redirects ({self.max_redirects})")

                            next_url = urljoin(current_url, location)

                            # Disallow HTTPS -> HTTP downgrade
                            if current_url.startswith("https://") and next_url.startswith("http://"):
                                raise SsrfViolationError("Disallowed HTTPS to HTTP downgrade redirect")

                            logger.info(f"SafeWebFetcher redirecting from {current_url} to {next_url}")
                            current_url = next_url
                            continue

                        # 3. Check HTTP status code
                        if response.status_code >= 400:
                            raise ContentUnavailableError(f"Remote server returned HTTP {response.status_code}")

                        # 4. Enforce Content-Type
                        content_type_header = response.headers.get("content-type", "text/html").lower()
                        is_allowed_type = any(allowed in content_type_header for allowed in ALLOWED_CONTENT_TYPES)
                        if not is_allowed_type:
                            raise UnsupportedContentTypeError(f"Unsupported Content-Type: '{content_type_header}'")

                        # 5. Stream body with hard cap
                        chunks: List[bytes] = []
                        total_bytes = 0
                        async for chunk in response.aiter_bytes():
                            total_bytes += len(chunk)
                            if total_bytes > self.max_bytes:
                                raise ContentTooLargeError(
                                    f"Response exceeded maximum allowed size of {self.max_bytes} bytes"
                                )
                            chunks.append(chunk)

                        raw_bytes = b"".join(chunks)

                        # Decode text safely with fallback
                        encoding = response.encoding or "utf-8"
                        try:
                            decoded_text = raw_bytes.decode(encoding, errors="replace")
                        except Exception:
                            decoded_text = raw_bytes.decode("utf-8", errors="replace")

                        return decoded_text, content_type_header, current_url

                except httpx.TimeoutException:
                    raise FetchTimeoutError(f"Request to '{current_url}' timed out after {self.timeout_seconds}s")
                except (httpx.ConnectError, httpx.RequestError) as e:
                    raise ContentUnavailableError(f"Connection failed for '{current_url}': {str(e)}")
