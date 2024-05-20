package com.mcp.crispy.franchise.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FranchiseTelDto {
    private int frnNo;
    private int empNo;
    private String frnTel;
    private Date createDt;
    private Date modifyDt;
}