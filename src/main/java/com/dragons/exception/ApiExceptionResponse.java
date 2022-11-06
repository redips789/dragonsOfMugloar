package com.dragons.exception;

import com.dragons.shared.JsonFormatPattern;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ApiExceptionResponse {

    private final int statusCode;
    @JsonFormat(pattern = JsonFormatPattern.TIMESTAMP_FORMAT)
    private final LocalDateTime timestamp;
    private final String reason;
    private final List<ApiExceptionDetails> exceptions;

    @JsonCreator
    private ApiExceptionResponse(int statusCode, LocalDateTime timestamp, String reason, List<ApiExceptionDetails> exceptions) {
        this.statusCode = statusCode;
        this.timestamp = timestamp;
        this.reason = reason;
        this.exceptions = exceptions;
    }

    public static ApiExceptionResponse of(int statusCode, LocalDateTime timestamp, String reason, List<ApiExceptionDetails> apiExceptionDetails) {
        return new ApiExceptionResponse(statusCode, timestamp, reason, apiExceptionDetails);
    }

    public static ApiExceptionResponse ofApiException(ApiException apiException) {
        return new ApiExceptionResponse(apiException.getStatusCode(), apiException.getTimestamp(), apiException.getReason(), apiException.getExceptions());
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    public LocalDateTime getTimestamp() {
        return this.timestamp;
    }

    public String getReason() {
        return this.reason;
    }

    public List<ApiExceptionDetails> getExceptions() {
        return this.exceptions;
    }
}