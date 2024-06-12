package com.mcp.crispy.email.dto;

import lombok.Getter;

@Getter
public enum VerifyStat {
    NEW(0, "신규"),
    USED(1, "사용"),
    EXPIRED(2, "만료");

    private final int value;
    private final String description;
    VerifyStat(int value, String description) {
        this.value = value;
        this.description = description;
    }

    public static VerifyStat of(int value) {
        for(VerifyStat stat : VerifyStat.values()) {
            if(stat.value == value) {
                return stat;
            }
        }
        throw new IllegalArgumentException("올바르지 못한 타입입니다:" + value);
    }

}
