package com.connectoBackend.common.exception;

/**
 * Thrown when the authenticated user does not have permission.
 */
public class ForbiddenException extends RuntimeException {

    public ForbiddenException(String message) {
        super(message);
    }
}