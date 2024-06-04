package com.mcp.crispy.employee.dto;

import com.mcp.crispy.common.annotation.NotBlankAndPattern;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeRegisterDto {
    private int empNo;

    @NotBlankAndPattern(
        notBlankMessage = "아이디를 입력해주세요.",
        patternMessage = "아이디는 영어 소문자와 숫자만\n사용하여 5~20자리여야 합니다.",
        pattern = "^[a-z0-9]{5,20}$"
    )
    private String empId;
    private String empPw;

    @NotBlankAndPattern(
        notBlankMessage = "직원 이름을 입력하세요.",
        patternMessage = "이름이 올바른지 확인해주세요.",
        pattern = "^[가-힣a-zA-Z\\s]+$"
    )
    private String empName;

    @NotBlankAndPattern(
        notBlankMessage = "이메일을 입력해주세요.",
        patternMessage = "올바른 이메일 형식을 입력해주세요.",
        pattern = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$"
    )
    private String empEmail;

    @Pattern(
            regexp = "^010\\d{3,4}\\d{4}$|^$",
            message = "휴대폰 번호 형식을 확인해주세요."
    )
    private String empPhone;
    private String empProfile;
    private EmpStatus empStat;

    private Date empInDt;
    private Position posNo;
    private int frnNo;
}
