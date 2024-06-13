package com.mcp.crispy.notification.dto;

import lombok.Getter;

@Getter
public enum NotifyCt {
    // 0: 발주 1: 휴가 2: 휴직 3: 승인 4: 반려
    ORDER(0, "발주"),
    VACATION(1, "휴가"),
    LEAVE_OF_ABSENCE(2, "휴직"),
    APPROVAL(3, "승인"),
    REJECTION(4, "반려");

    private final int value;
    private final String description;

    NotifyCt(int value, String description) {
        this.value = value;
        this.description = description;
    }

    public static NotifyCt of(int value) {
        for (NotifyCt cate : values()) {
            if (cate.value == value) {
                return cate;
            }
        }
        throw new IllegalArgumentException("확인되지 않은 타입입니다." + value);
    }


}
