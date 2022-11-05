package com.dragons.exception;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ApiExceptionDetails {

    private final String code;
    private final String message;
    private final String fieldName;

    @JsonCreator
    public ApiExceptionDetails(String code, String message, String fieldName) {
        this.code = code;
        this.message = message;
        this.fieldName = fieldName;
    }

    public String getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }

    public String getFieldName() {
        return this.fieldName;
    }
}
