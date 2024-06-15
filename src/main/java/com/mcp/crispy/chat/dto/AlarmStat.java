package com.mcp.crispy.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AlarmStat {
    ACTIVE(0), // 알림 활성화 상태
    INACTIVE(1); // 알림 비활성화 상태

    private final int status;

    public static AlarmStat of(int status) {
        for (AlarmStat stat : AlarmStat.values()) {
            if (stat.status == status) {
                return stat;
            }
        }
        throw new IllegalArgumentException("올바르지 못한 상태입니다: " + status);
    }
}
