package com.mcp.crispy.employee.dto;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmpPhoneUpdateDto {
    private Integer empNo;
    @Pattern(regexp = "^010\\d{4}\\d{4}$|^$", message = "휴대폰 번호 형식을 확인해주세요.")
    private String empPhone;
    private Integer modifier;
}
