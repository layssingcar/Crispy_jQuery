package com.mcp.crispy.approval.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicantDto {
    private int empNo;          // 직원번호
    private String empName;     // 직원명
    private String empStreet;   // 도로명주소
    private String empDetail;   // 상세주소
    private int posNo;          // 직책번호
    private String posName;     // 직책명
}
