package com.mnesa.backend.common.exception;

import org.springframework.http.HttpStatus;

public class AuthenticationFailedException extends MnesaException {
    public AuthenticationFailedException(String message) {
        super(message, HttpStatus.UNAUTHORIZED, "AUTHENTICATION_FAILED");
    }
}
