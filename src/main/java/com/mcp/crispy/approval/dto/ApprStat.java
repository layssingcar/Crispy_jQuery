package com.mcp.crispy.approval.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ApprStat {
    WAITING(0, "대기"),
    ONGOING(1, "진행중"),
    APPROVING(2, "승인"),
    RETURNING(3, "반려");

    private final int code;
    private final String desciption;

    public static ApprStat of(int code) {
        for (ApprStat stat : values())
            if (stat.getCode() == code) return stat;

        throw new IllegalArgumentException("Unknown Stat Code: " + code);
    }
}
