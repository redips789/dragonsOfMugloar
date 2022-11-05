package com.example.exception;

import java.util.Date;
import java.util.Optional;

public class SpringDefaultExceptionResponse {

    private final Date timestamp;
    private final Integer status;
    private final String error;
    private final String exception;
    private final String message;
    private final String path;

    public SpringDefaultExceptionResponse(Date timestamp, Integer status, String error, String exception, String message, String path) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.exception = exception;
        this.message = message;
        this.path = path;
    }

    public Date getTimestamp() {
        return this.timestamp;
    }

    public Integer getStatus() {
        return this.status;
    }

    public String getError() {
        return this.error;
    }

    public Optional<String> getMessage() {
        return Optional.ofNullable(this.message);
    }

    public Optional<String> getException() {
        return Optional.ofNullable(this.exception);
    }

    public String getPath() {
        return this.path;
    }
}