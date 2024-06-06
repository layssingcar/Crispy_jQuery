package com.mcp.crispy.approval.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApprovalDto {
    private int timeOffTempNo;      // 휴가휴직임시번호
    private int timeOffCtNo;        // 문서카테고리
    private String timeOffStartDt;  // 시작일
    private String timeOffEndDt;    // 종료일
    private int timeOffPeriod;      // 기간
    private String timeOffContent;  // 문서내용
    private int empNo;              // 직원번호
}
