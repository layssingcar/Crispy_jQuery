package com.mcp.crispy.sales.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalesDto {

    /* SALES_T 매출 테이블 */
    private int salNo;
    private Double salPrice;
    private Date createDt;
    private int creator;
    private Date modifyDt;
    private int modifier;
    private int frnNo;

    /* 매출 날짜 정리 */
    private Date salDt;     // 전체
    private String salDd;   // 일별
    private String salMn;   // 달별
    private String salYr;   // 년도별

    /* FRANCHISE_T 매장 테이블 */
    private String frnName;
    private String frnOwner;
    private String frnImg;
    private String frnGu;

    /* EMPLOYEE_T 사용자 정보 */
    private String empId;
    private String empName;

    /* 매출 총합 정보 */
    private Double totalAvgSales;
    private Double totalSales;
}
