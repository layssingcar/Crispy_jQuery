package com.mcp.crispy.employee.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OwnerRegisterDto {
    private int empNo;


    private String empPw;
    private String empName;

    @NotBlank(message = "아이디를 입력해주세요.")
    @Pattern(regexp = "^[a-z0-9]{5,20}$", message = "아이디는 영어 소문자와\n숫자만 사용하여 5~20자리여야 합니다.")
    private String empId;

    @NotBlank(message = "이메일을 입력해주세요.")
    @Pattern(regexp = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$", message = "올바른 이메일 형식을 입력해주세요.")
    private String empEmail;

    @NotBlank(message = "휴대폰번호를 입력해주세요.")
    @Pattern(regexp = "^010\\d{3,4}\\d{4}$", message = "휴대폰 번호 형식을 확인해주세요.")
    private String empPhone;
    private String empProfile;
    private EmpStatus empStatus;
    private int posNo;
    private int frnNo;

}
