package com.mcp.crispy.employee.dto;

import com.mcp.crispy.common.annotation.NotBlankAndPattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeNameUpdateDto {
    private Integer empNo;
    private Integer modifier;

    @NotBlankAndPattern(
        notBlankMessage = "직원 이름을 입력하세요.",
        patternMessage = "직원 이름에는 숫자를 포함할 수 없습니다.",
        pattern = "^[가-힣]+$"
    )
    private String empName;
}
