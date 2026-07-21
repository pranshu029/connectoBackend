package com.connectoBackend.common.exception;

/**
 * Thrown when a request conflicts with existing data.
 */
public class ConflictException extends RuntimeException {

	public ConflictException(String message) {
		super(message);
	}

}
