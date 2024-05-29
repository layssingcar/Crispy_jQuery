package com.mcp.crispy.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CrEmpDto { //채팅방 참가자 DTO
    private Integer chatRoomNo;
    private Integer empNo;
    private String empName;
    private String empId;
    private String empProfile;
    private EntryStat entryStat;
    private AlarmStat alarmStat;
}
