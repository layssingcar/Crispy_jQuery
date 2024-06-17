package com.mcp.crispy.approval.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApprLineDto {
    private int apprLineNo;         // 결재선번호
    private int apprLineOrder;      // 결재순서
    private int apprLineStat;       // 결재상태
    private String apprLineDt;      // 결재일
    private String apprSign;        // 결재서명 (결재자)
    private String apprLineReason;  // 반려사유

    private int apprNo;             // 문서번호
    private int empNo;              // 직원번호
    private String empName;         // 직원명
    private String empSign;         // 결재서명 (기안자)
    private int posNo;              // 직책번호
    private String posName;         // 직책명
    private int adminNo;            // 관리자번호

    private int creator;            // 생성자
}
