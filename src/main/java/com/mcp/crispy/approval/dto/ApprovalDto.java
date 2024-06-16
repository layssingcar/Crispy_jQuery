package com.mcp.crispy.approval.dto;

import com.mcp.crispy.stock.dto.StockDto;
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
    /* 전자결재 */
    private int apprNo;             // 문서번호
    private String apprDt;          // 기안일
    private int apprStat;           // 문서상태번호
    private String apprStatName;    // 문서상태명
    private Integer empNo;              // 직원번호
    private String empName;         // 직원명
    private int creator;            // 생성자

    /* 휴가,휴직신청서 */
    private int timeOffCtNo;        // 문서카테고리번호
    private String timeOffCtName;   // 문서카테고리명
    private String timeOffStartDt;  // 시작일
    private String timeOffEndDt;    // 종료일
    private int timeOffPeriod;      // 기간
    private String timeOffContent;  // 문서내용

    /* 발주신청서 */
    private int orderCost;  // 합계금액

    /* 발주재고, 결재선 */
    private List<StockDto> stockOrderList;      // 발주재고 리스트
    private List<ApprLineDto> apprLineDtoList;  // 결재선 리스트

    /* 임시저장 */
    private int timeOffTempNo;  // 휴가휴직임시번호
    private int orderTempNo;    // 발주임시번호

    /* 직원 */
    private String empStreet;   // 도로명주소
    private String empDetail;   // 상세주소
    private String empSign;     // 결재서명
    private int posNo;          // 직책번호
    private String posName;     // 직책명

    /* 가맹점 */
    private int frnNo;          // 가맹점번호
    private String frnName;     // 가맹점명
    private String frnOwner;    // 대표자
    private String frnTel;      // 전화번호
    private String frnStreet;   // 도로명주소
    private String frnDetail;   // 상세주소
}
