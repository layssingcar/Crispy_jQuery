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
public class EmployeeEmailUpdateDto {
    private Integer empNo;
    private Integer modifier;

    @NotBlankAndPattern(
            notBlankMessage = "이메일을 입력해주세요.",
            patternMessage = "올바른 이메일 형식을 입력해주세요.",
            pattern = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$"
    )
    private String empEmail;
}
