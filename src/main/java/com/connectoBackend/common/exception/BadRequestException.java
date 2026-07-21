package com.connectoBackend.common.exception;

/**
 * Thrown when the client sends an invalid request.
 */
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}