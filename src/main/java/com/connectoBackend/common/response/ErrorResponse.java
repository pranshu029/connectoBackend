package com.connectoBackend.common.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Builder
public class ErrorResponse {

    @Builder.Default
    private final LocalDateTime timestamp = LocalDateTime.now();

    private final boolean success;

    private final int status;

    private final String error;

    private final String message;

    private final String path;

    // -> Field validation errors
    private final Map<String, String> validationErrors;
}