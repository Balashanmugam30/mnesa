package com.mnesa.backend.modules.intake.service;

import com.mnesa.backend.common.exception.MnesaException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UrlSanitizerServiceTest {

    private UrlSanitizerService urlSanitizer;

    @BeforeEach
    void setUp() {
        urlSanitizer = new UrlSanitizerService();
    }

    @Test
    @DisplayName("Should extract first URL from complex conversational text")
    void testExtractFirstUrl() {
        String text = "Check out this internship at https://careers.google.com/jobs/12345! Apply soon.";
        String extracted = urlSanitizer.extractFirstUrl(text);
        assertEquals("https://careers.google.com/jobs/12345", extracted);
    }

    @Test
    @DisplayName("Should return null if no URL present in text")
    void testExtractFirstUrlNone() {
        assertNull(urlSanitizer.extractFirstUrl("Just a regular message with no links."));
        assertNull(urlSanitizer.extractFirstUrl(null));
        assertNull(urlSanitizer.extractFirstUrl("   "));
    }

    @Test
    @DisplayName("Should strip tracking parameters (utm, fbclid) while preserving meaningful query params")
    void testSanitizeAndNormalizeTrackingParams() {
        String raw = "https://example.com/apply?utm_source=linkedin&utm_medium=social&jobId=9876&fbclid=abcdef123#overview";
        String normalized = urlSanitizer.sanitizeAndNormalize(raw);

        assertEquals("https://example.com/apply?jobId=9876", normalized);
    }

    @Test
    @DisplayName("Should reject non-HTTP/HTTPS protocols")
    void testRejectUnsupportedProtocols() {
        assertThrows(MnesaException.class, () -> urlSanitizer.sanitizeAndNormalize("file:///etc/passwd"));
        assertThrows(MnesaException.class, () -> urlSanitizer.sanitizeAndNormalize("javascript:alert(1)"));
        assertThrows(MnesaException.class, () -> urlSanitizer.sanitizeAndNormalize("ftp://example.com/file"));
    }

    @Test
    @DisplayName("Should block localhost and internal loopback addresses (SSRF defense)")
    void testBlockSsrfLocalhost() {
        assertThrows(MnesaException.class, () -> urlSanitizer.sanitizeAndNormalize("http://localhost:8080/admin"));
        assertThrows(MnesaException.class, () -> urlSanitizer.sanitizeAndNormalize("http://127.0.0.1/private"));
        assertThrows(MnesaException.class, () -> urlSanitizer.sanitizeAndNormalize("http://169.254.169.254/latest/meta-data/"));
    }

    @Test
    @DisplayName("Should cleanly extract source domain")
    void testExtractDomain() {
        assertEquals("linkedin.com", urlSanitizer.extractDomain("https://www.linkedin.com/jobs/view/123"));
        assertEquals("careers.google.com", urlSanitizer.extractDomain("https://careers.google.com/jobs/results/456"));
        assertNull(urlSanitizer.extractDomain(null));
    }
}
