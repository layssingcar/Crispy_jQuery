package com.mcp.crispy.email.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailVerificationDto {

    private String verifyEmail;
    private String verifyCode;
    /**
     *  0: NEW
     *  1: USED
     *  2: EXPIRED
     */
    private int verifyStat;
}
