package com.mnesa.backend.common.exception;

import org.springframework.http.HttpStatus;

public class InvalidTokenException extends MnesaException {
    public InvalidTokenException(String message) {
        super(message, HttpStatus.UNAUTHORIZED, "INVALID_TOKEN");
    }
}
