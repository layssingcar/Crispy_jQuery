package com.mcp.crispy.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EmpStatus {
    EMPLOYED(0, "재직"),
    ON_LEAVE(1, "휴직"),
    RETIRED(2, "퇴직");

    private final int value;
    private final String description;

    public static EmpStatus fromValue(Integer value) {
        if (value == null) {
            return EmpStatus.EMPLOYED;
        }
        for (EmpStatus status : EmpStatus.values()) {
            if (status.getValue() == value) {
                return status;
            }
        }
        return EmpStatus.EMPLOYED;
    }
}

