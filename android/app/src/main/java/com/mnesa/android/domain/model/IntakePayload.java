package com.mnesa.android.domain.model;

/**
 * Domain model representing raw parsed share intent data before submission.
 */
public class IntakePayload {

    private final String rawText;
    private final String extractedUrl;
    private final String sourceDomain;
    private final String sourceType;
    private final String imageUri;
    private final String mimeType;
    private final long fileSizeBytes;
    private final boolean valid;
    private final String validationError;

    public IntakePayload(String rawText,
                         String extractedUrl,
                         String sourceDomain,
                         String sourceType,
                         String imageUri,
                         String mimeType,
                         long fileSizeBytes,
                         boolean valid,
                         String validationError) {
        this.rawText = rawText;
        this.extractedUrl = extractedUrl;
        this.sourceDomain = sourceDomain;
        this.sourceType = sourceType;
        this.imageUri = imageUri;
        this.mimeType = mimeType;
        this.fileSizeBytes = fileSizeBytes;
        this.valid = valid;
        this.validationError = validationError;
    }

    public String getRawText() { return rawText; }
    public String getExtractedUrl() { return extractedUrl; }
    public String getSourceDomain() { return sourceDomain; }
    public String getSourceType() { return sourceType; }
    public String getImageUri() { return imageUri; }
    public String getMimeType() { return mimeType; }
    public long getFileSizeBytes() { return fileSizeBytes; }
    public boolean isValid() { return valid; }
    public String getValidationError() { return validationError; }
}
