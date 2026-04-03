package com.nocode.admin.exception;

/**
 * Resource not found exception
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resourceType, Long id) {
        super(resourceType + " not found: " + id);
    }

    public ResourceNotFoundException(String resourceType, String identifier) {
        super(resourceType + " not found: " + identifier);
    }
}