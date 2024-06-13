package com.mcp.crispy.approval.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TimeOffCtNo {
    VACATION(0, "휴가신청서"),
    LEAVE_OF_ABSENCE(1, "휴직신청서");

    private final int code;
    private final String desciption;

    public static TimeOffCtNo of(int code) {
        for (TimeOffCtNo ct : values())
            if (ct.getCode() == code) return ct;

        throw new IllegalArgumentException("Unknown Ct Code: " + code);
    }
}
