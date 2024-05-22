package com.mcp.crispy.email.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailVerificationDto {

    private int verifyNo;
    private String verifyEmail;
    private String verifyCode;
    private Date verifyEndDt;
    /**
     *  0: NEW
     *  1: USED
     *  2: EXPIRED
     */
    private int verifyStat;
}
