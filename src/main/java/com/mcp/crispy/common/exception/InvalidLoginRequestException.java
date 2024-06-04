package com.mcp.crispy.common.exception;

import lombok.Getter;

import java.util.Map;

@Getter
public class InvalidLoginRequestException extends RuntimeException{
    private final Map<String, String> errors;

    public InvalidLoginRequestException(Map<String, String> errors) {
        this.errors = errors;
    }

}
