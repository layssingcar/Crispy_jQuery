package com.mcp.crispy.approval.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApprovalDto {
    private int apprNo;     // 문서번호
    private String apprDt;  // 기안일
    private int apprStat;   // 문서상태
    private int empNo;      // 직원번호

    private List<ApprLineDto> apprLineDtoList;  // 결재선 리스트

    private int timeOffCtNo;        // 문서카테고리
    private String timeOffStartDt;  // 시작일
    private String timeOffEndDt;    // 종료일
    private int timeOffPeriod;      // 기간
    private String timeOffContent;  // 문서내용

    private int timeOffTempNo;  // 휴가휴직임시번호
}
