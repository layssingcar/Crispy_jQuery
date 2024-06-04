package com.mcp.crispy.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminDto {
    private int adminNo;
    private String adminId;
    private String adminPw;
    private String accessToken;
    private String refreshToken;
}
