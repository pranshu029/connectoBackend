package com.connectoBackend.common.exception;

/**
 * Thrown when authentication fails.
 */
public class AuthenticationException extends RuntimeException {

    public AuthenticationException(String message) {
        super(message);
    }

}