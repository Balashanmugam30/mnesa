package com.mnesa.android.domain;

import android.content.Intent;
import com.mnesa.android.domain.model.IntakePayload;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class IntakePayloadParserTest {

    private IntakePayloadParser parser;

    @Mock
    private Intent intent;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        parser = new IntakePayloadParser();
    }

    @Test
    public void testParseNullIntentReturnsInvalid() {
        IntakePayload payload = parser.parse(null, null);
        assertNotNull(payload);
        assertFalse(payload.isValid());
        assertEquals("UNSUPPORTED", payload.getSourceType());
    }

    @Test
    public void testParseUrlTextRecognizesUrlSource() {
        when(intent.getAction()).thenReturn(Intent.ACTION_SEND);
        when(intent.getType()).thenReturn("text/plain");
        when(intent.getStringExtra(Intent.EXTRA_TEXT)).thenReturn("https://careers.google.com/jobs/12345");

        IntakePayload payload = parser.parse(intent, null);
        assertNotNull(payload);
        assertTrue(payload.isValid());
        assertEquals("URL", payload.getSourceType());
        assertEquals("https://careers.google.com/jobs/12345", payload.getExtractedUrl());
        assertEquals("careers.google.com", payload.getSourceDomain());
    }

    @Test
    public void testParseHybridTextWithEmbeddedUrl() {
        when(intent.getAction()).thenReturn(Intent.ACTION_SEND);
        when(intent.getType()).thenReturn("text/plain");
        when(intent.getStringExtra(Intent.EXTRA_TEXT))
                .thenReturn("Apply for this Google SWE internship opportunity: https://careers.google.com/jobs/12345! Highly recommended.");

        IntakePayload payload = parser.parse(intent, null);
        assertNotNull(payload);
        assertTrue(payload.isValid());
        assertEquals("HYBRID", payload.getSourceType());
        assertEquals("https://careers.google.com/jobs/12345", payload.getExtractedUrl());
    }

    @Test
    public void testParsePlainTextWithoutUrl() {
        when(intent.getAction()).thenReturn(Intent.ACTION_SEND);
        when(intent.getType()).thenReturn("text/plain");
        when(intent.getStringExtra(Intent.EXTRA_TEXT))
                .thenReturn("Hackathon meeting notes: submission deadline next Friday at 5pm.");

        IntakePayload payload = parser.parse(intent, null);
        assertNotNull(payload);
        assertTrue(payload.isValid());
        assertEquals("TEXT", payload.getSourceType());
        assertNull(payload.getExtractedUrl());
    }

    @Test
    public void testExtractDomainRemovesWww() {
        assertEquals("linkedin.com", parser.extractDomain("https://www.linkedin.com/jobs/view/123"));
        assertEquals("x.com", parser.extractDomain("https://x.com/internships/status/456"));
    }

    @Test
    public void testParseIntentDataFallbackWhenExtraTextNull() {
        when(intent.getAction()).thenReturn(Intent.ACTION_SEND);
        when(intent.getType()).thenReturn("text/plain");
        when(intent.getStringExtra(Intent.EXTRA_TEXT)).thenReturn(null);
        android.net.Uri mockUri = org.mockito.Mockito.mock(android.net.Uri.class);
        when(intent.getData()).thenReturn(mockUri);
        when(intent.getDataString()).thenReturn("https://devpost.com/hackathons/example");

        IntakePayload payload = parser.parse(intent, null);
        assertNotNull(payload);
        assertTrue(payload.isValid());
        assertEquals("URL", payload.getSourceType());
        assertEquals("https://devpost.com/hackathons/example", payload.getExtractedUrl());
    }

    @Test
    public void testParseWithSubjectEnriched() {
        when(intent.getAction()).thenReturn(Intent.ACTION_SEND);
        when(intent.getType()).thenReturn("text/plain");
        when(intent.getStringExtra(Intent.EXTRA_SUBJECT)).thenReturn("Google Summer Internship 2026");
        when(intent.getStringExtra(Intent.EXTRA_TEXT)).thenReturn("https://careers.google.com/jobs/results/999");

        IntakePayload payload = parser.parse(intent, null);
        assertNotNull(payload);
        assertTrue(payload.isValid());
        assertTrue(payload.getRawText().contains("Google Summer Internship 2026"));
        assertEquals("https://careers.google.com/jobs/results/999", payload.getExtractedUrl());
    }

    @Test
    public void testExtractUrlWithWwwFallback() {
        String url = parser.extractUrl("Check out this opportunity: www.devpost.com/hackathons/ai-summit!");
        assertEquals("https://www.devpost.com/hackathons/ai-summit", url);
    }
}
