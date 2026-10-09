package com.mnesa.android.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class IntakeRequestDto {

    @SerializedName("idempotencyKey")
    private String idempotencyKey;

    @SerializedName("text")
    private String text;

    @SerializedName("url")
    private String url;

    @SerializedName("sourceType")
    private String sourceType;

    @SerializedName("clientCaptureId")
    private String clientCaptureId;

    @SerializedName("mediaMimeType")
    private String mediaMimeType;

    @SerializedName("mediaSizeBytes")
    private Long mediaSizeBytes;

    @SerializedName("metadata")
    private String metadata;

    public IntakeRequestDto() {}

    public IntakeRequestDto(String idempotencyKey, String text, String url, String sourceType,
                            String clientCaptureId, String mediaMimeType, Long mediaSizeBytes, String metadata) {
        this.idempotencyKey = idempotencyKey;
        this.text = text;
        this.url = url;
        this.sourceType = sourceType;
        this.clientCaptureId = clientCaptureId;
        this.mediaMimeType = mediaMimeType;
        this.mediaSizeBytes = mediaSizeBytes;
        this.metadata = metadata;
    }

    public String getIdempotencyKey() { return idempotencyKey; }
    public void setIdempotencyKey(String idempotencyKey) { this.idempotencyKey = idempotencyKey; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getSourceType() { return sourceType; }
    public void setSourceType(String sourceType) { this.sourceType = sourceType; }

    public String getClientCaptureId() { return clientCaptureId; }
    public void setClientCaptureId(String clientCaptureId) { this.clientCaptureId = clientCaptureId; }

    public String getMediaMimeType() { return mediaMimeType; }
    public void setMediaMimeType(String mediaMimeType) { this.mediaMimeType = mediaMimeType; }

    public Long getMediaSizeBytes() { return mediaSizeBytes; }
    public void setMediaSizeBytes(Long mediaSizeBytes) { this.mediaSizeBytes = mediaSizeBytes; }

    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }
}
