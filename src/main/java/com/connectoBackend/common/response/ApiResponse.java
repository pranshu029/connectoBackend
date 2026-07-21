package com.connectoBackend.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

      //   Standard API response wrapper for all successful requests.
      // @param <T> the type of response data
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    // -> Indicates whether the request was successful
    private final Boolean success;

    // -> Response message
    private final String message;

    // -> Response payload
    private final T data;

    // -> Response generation timestamp
    @Builder.Default
    private final LocalDateTime timestamp = LocalDateTime.now();
}