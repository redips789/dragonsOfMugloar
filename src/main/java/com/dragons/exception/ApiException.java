package com.dragons.exception;

import com.dragons.shared.TimeMachine;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

public class ApiException extends RuntimeException {

    private final int statusCode;
    private final LocalDateTime timestamp;
    private final String reason;
    private final List<ApiExceptionDetails> exceptions;

    private ApiException(HttpStatus status, LocalDateTime timestamp, String reason, List<ApiExceptionDetails> exceptions) {
        this(status.value(), timestamp, reason, exceptions);
    }

    private ApiException(int statusCode, LocalDateTime timestamp, String reason, List<ApiExceptionDetails> exceptions) {
        super(reason);
        this.statusCode = statusCode;
        this.timestamp = timestamp;
        this.reason = reason;
        this.exceptions = exceptions;
    }

    public static ApiException ofExceptions(HttpStatus status, String reason, ApiExceptionDetails... exceptions) {
        return new ApiException(status.value(), TimeMachine.nowLocalDateAndTime(), reason, List.of(exceptions));
    }

    public static ApiException ofExceptions(HttpStatus status, String reason, List<ApiExceptionDetails> exceptions) {
        return new ApiException(status.value(), TimeMachine.nowLocalDateAndTime(), reason, exceptions);
    }

    public static ApiException internalServerError(String reason, ApiExceptionDetails... exceptions) {
        return new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, TimeMachine.nowLocalDateAndTime(), reason, List.of(exceptions));
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
