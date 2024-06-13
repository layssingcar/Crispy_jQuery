package com.mcp.crispy.notification.dto;

import lombok.Getter;

@Getter
public enum NotifyStat {
    // 0 : 안 읽음, 1 : 읽음
    NOT_READ(0, "안 읽음"),
    READ(1, "읽음");

    private final int value;
    private final String description;

    NotifyStat(int value, String description) {
        this.value = value;
        this.description = description;
    }

    public static NotifyStat of(int value) {
        for (NotifyStat status : values()) {
            if (status.value == value) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown value: " + value);
    }

}
