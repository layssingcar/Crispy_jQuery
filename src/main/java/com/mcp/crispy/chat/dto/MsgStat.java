package com.mcp.crispy.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MsgStat {
    ACTIVE(0), // 메시지 활성화 상태
    DELETED(1); // 메시지 비활성화 상태

    private final int status;

    public static MsgStat of(int status) {
        for (MsgStat stat : MsgStat.values()) {
            if (stat.status == status) {
                return stat;
            }
        }
        throw new IllegalArgumentException("올바르지 못한 상태입니다: " + status);
    }
}
