package com.mcp.crispy.approval.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApprOptionDto {
    private String type;        // 문서타입 (기안,결재)
    private int empNo;          // 직원번호
    private int pageNo;         // 페이지번호
    private int timeOffCtNo;    // 문서카테고리
    private int apprStat;       // 문서상태
}
