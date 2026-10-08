package com.mnesa.backend.common.exception;

import org.springframework.http.HttpStatus;

public class DuplicateResourceException extends MnesaException {
    public DuplicateResourceException(String message) {
        super(message, HttpStatus.CONFLICT, "DUPLICATE_RESOURCE");
    }
}
