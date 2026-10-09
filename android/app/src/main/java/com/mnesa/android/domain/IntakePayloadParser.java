package com.mnesa.android.domain;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import com.mnesa.android.domain.model.IntakePayload;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Robust parser converting incoming Android Intent data into validated IntakePayloads.
 */
public class IntakePayloadParser {

    public static final long MAX_IMAGE_SIZE_BYTES = 15 * 1024 * 1024; // 15MB

    private static final Pattern URL_PATTERN = Pattern.compile(
            "https?://[a-zA-Z0-9\\-\\._~:/?#\\[\\]@!$&'()*+,;=%]+",
            Pattern.CASE_INSENSITIVE
    );

    public IntakePayload parse(Intent intent, ContentResolver contentResolver) {
        if (intent == null) {
            return new IntakePayload(null, null, null, "UNSUPPORTED", null, null, 0, false, "Intent was null");
        }

        String action = intent.getAction();
        if (!Intent.ACTION_SEND.equals(action) && !Intent.ACTION_SEND_MULTIPLE.equals(action)) {
            return new IntakePayload(null, null, null, "UNSUPPORTED", null, null, 0, false, "Unsupported action: " + action);
        }

        String mimeType = intent.getType();
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText == null) {
            CharSequence charSequence = intent.getCharSequenceExtra(Intent.EXTRA_TEXT);
            if (charSequence != null) {
                sharedText = charSequence.toString();
            }
        }

        // Image stream handling
        Uri imageUri = null;
        if (intent.hasExtra(Intent.EXTRA_STREAM)) {
            try {
                imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
            } catch (Exception ignored) {}
        }

        // Extract URL from shared text if present
        String extractedUrl = null;
        String sourceDomain = null;
        if (sharedText != null && !sharedText.isBlank()) {
            extractedUrl = extractUrl(sharedText);
            if (extractedUrl != null) {
                sourceDomain = extractDomain(extractedUrl);
            }
        }

        long fileSizeBytes = 0;
        if (imageUri != null && contentResolver != null) {
            fileSizeBytes = queryFileSize(contentResolver, imageUri);
            if (fileSizeBytes > MAX_IMAGE_SIZE_BYTES) {
                return new IntakePayload(
                        sharedText, extractedUrl, sourceDomain, "IMAGE",
                        imageUri.toString(), mimeType, fileSizeBytes, false,
                        "Image size exceeds 15MB threshold (" + (fileSizeBytes / (1024 * 1024)) + "MB)"
                );
            }
        }

        // Determine source type
        String sourceType;
        if (imageUri != null) {
            sourceType = (extractedUrl != null || (sharedText != null && sharedText.length() > 40))
                    ? "HYBRID"
                    : "IMAGE";
        } else if (extractedUrl != null) {
            sourceType = (sharedText == null || sharedText.trim().equalsIgnoreCase(extractedUrl) || sharedText.length() <= 80)
                    ? "URL"
                    : "HYBRID";
        } else if (sharedText != null && !sharedText.isBlank()) {
            sourceType = "TEXT";
        } else {
            return new IntakePayload(null, null, null, "UNSUPPORTED", null, null, 0, false, "No text or image content found in share payload");
        }

        return new IntakePayload(
                sharedText,
                extractedUrl,
                sourceDomain,
                sourceType,
                imageUri != null ? imageUri.toString() : null,
                mimeType,
                fileSizeBytes,
                true,
                null
        );
    }

    public String extractUrl(String text) {
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

    public String extractDomain(String rawUrl) {
        if (rawUrl == null || rawUrl.isBlank()) {
            return null;
        }
        try {
            java.net.URI uri = java.net.URI.create(rawUrl.trim());
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

    private long queryFileSize(ContentResolver contentResolver, Uri uri) {
        if (uri == null || contentResolver == null) {
            return 0;
        }
        Cursor cursor = null;
        try {
            cursor = contentResolver.query(uri, new String[]{OpenableColumns.SIZE}, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                if (sizeIndex != -1 && !cursor.isNull(sizeIndex)) {
                    return cursor.getLong(sizeIndex);
                }
            }
        } catch (Exception ignored) {
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return 0;
    }
}
