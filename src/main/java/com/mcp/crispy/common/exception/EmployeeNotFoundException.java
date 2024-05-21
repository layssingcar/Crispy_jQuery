package com.mcp.crispy.common.exception;

import lombok.Getter;

@Getter
public class EmployeeNotFoundException extends RuntimeException{

    private final String field;
    private final String message;

    public EmployeeNotFoundException(String field, String message){
        super(message);
        this.field = field;
        this.message = message;
    }
}
