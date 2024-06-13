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
    private String type;            // 문서타입 (기안,결재)
    private int empNo;              // 직원번호
    private int frnNo;              // 가맹점번호
    private int pageNo;             // 페이지번호
    private int timeOffCtNo;        // 문서카테고리구분
    private int apprStat;           // 문서상태구분
    private String apprDtSort;      // 기안일정렬
    private String sortKey;         // 정렬기준
    private String sortOrder;       // 정렬순서
    private String empName;         // 기안자검색
    private String searchKeyword;   // 검색키워드 (가맹점명, 대표자)
}
