package com.mcp.crispy.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Position {
    OWNER(0, "점주"),   // 0: 점주
    MANAGER(1 ,"매니저"), // 1: 메니저
    EMPLOYEE(2 ,"직원"); // 2: 직원

    private final int code;
    private final String description;


    public static Position of(int code) {
        for (Position pos : values()) {
            if (pos.code == code) {
                return pos;
            }
        }
        throw new IllegalArgumentException("Unknown Position code: " + code);
    }
}