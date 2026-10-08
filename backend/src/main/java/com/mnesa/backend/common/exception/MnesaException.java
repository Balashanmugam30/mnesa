package com.mnesa.backend.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Base domain exception for MNESA business logic failures.
 */
public class MnesaException extends RuntimeException {

    private final HttpStatus status;
    private final String errorCode;

    public MnesaException(String message, HttpStatus status, String errorCode) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }

    public MnesaException(String message, Throwable cause, HttpStatus status, String errorCode) {
        super(message, cause);
        this.status = status;
        this.errorCode = errorCode;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
