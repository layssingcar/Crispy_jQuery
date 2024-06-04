package com.mcp.crispy.common.exception;

import lombok.Getter;

@Getter
public class PasswordException extends RuntimeException{

    private final String field;
    private final String message;

    public PasswordException(String field, String message) {
        super(message);
        this.field = field;
        this.message = message;
    }
}
