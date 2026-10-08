package com.mnesa.android.data.remote.dto;

public class ApiResponseDto<T> {
    private boolean success;
    private String message;
    private T data;
    private String correlationId;

    public ApiResponseDto() {}

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }

    public String getCorrelationId() { return correlationId; }
    public void setCorrelationId(String correlationId) { this.correlationId = correlationId; }
}
