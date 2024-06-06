package com.mcp.crispy.employee.dto;

import com.mcp.crispy.common.annotation.NotBlankAndPattern;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FindEmployeeDto {

    @NotBlankAndPattern (
            notBlankMessage = "아이디를 입력해주세요.",
            patternMessage = "아이디는 영어 소문자와 숫자만 사용하여 5~20자리여야 합니다.",
            pattern ="^[a-z0-9]{5,20}$"
    )
    private String empId;

    @NotBlankAndPattern (
            notBlankMessage = "직원 이름을 입력하세요.",
            patternMessage = "직원 이름에는 숫자를 포함할 수 없습니다.",
            pattern ="^[가-힣]+$"
    )
    private String empName;
    private String empPw;

    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "올바른 이메일 형식을 입력해주세요.")
    private String empEmail;
    private Date createDt;

    public LocalDate getCreateDtAsLocalDate() {
        return createDt.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }
}
