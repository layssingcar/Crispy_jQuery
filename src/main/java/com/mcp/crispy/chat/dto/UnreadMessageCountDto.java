package com.mcp.crispy.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnreadMessageCountDto {
    private Integer empNo;
    private Integer chatRoomNo;
    private Integer unreadCount;
}
