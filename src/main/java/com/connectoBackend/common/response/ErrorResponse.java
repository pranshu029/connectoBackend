package com.connectoBackend.common.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

//Standard API response for all error scenarios.

@Getter
@Builder
public class ErrorResponse {

    // -> Error generation timestamp
    @Builder.Default
    private final LocalDateTime timestamp = LocalDateTime.now();

    // -> HTTP status code
    private final int status;

    // -> HTTP status name
    private final String error;

    // -> Error message
    private final String message;

    // -> Requested API path
    private final String path;

    // -> Validation errors (if any)
    private final List<String> errors;
}