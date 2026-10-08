package com.mnesa.backend.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown when a requested resource does not exist.
 */
public class ResourceNotFoundException extends MnesaException {

    public ResourceNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND");
    }

    public ResourceNotFoundException(String resourceName, Object identifier) {
        super(String.format("%s with identifier '%s' was not found", resourceName, identifier),
                HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND");
    }

    public ResourceNotFoundException(String resourceName, String fieldName, Object identifier) {
        super(String.format("%s with %s '%s' was not found", resourceName, fieldName, identifier),
                HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND");
    }
}
