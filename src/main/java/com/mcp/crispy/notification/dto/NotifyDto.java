package com.mcp.crispy.notification.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotifyDto {
    private int notifyNo;
    private NotifyCt notifyCt; // 0: 발주 1: 휴가 2: 휴직 3: 승인 4: 반려
    private String notifyContent;
    private NotifyStat notifyStat; // 0 : 안 읽음, 1 : 읽음
    private Date createDt;
    private int creator;
    private Date modifyDt;
    private int modifier;
    private int empNo;
}
