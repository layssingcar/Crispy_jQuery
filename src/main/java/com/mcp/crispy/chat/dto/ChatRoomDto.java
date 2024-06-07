package com.mcp.crispy.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;


@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ChatRoomDto {
    private Integer chatRoomNo;
    private String chatRoomTitle;
    private Integer chatRoomStat;
    private Date createDt;
    private Integer creator;
    private Date modifyDt;
    private Integer modifier;
    private List<CrEmpDto> participants;
    private String empName;
    private Integer empNo;
    private String msgContent;
}
