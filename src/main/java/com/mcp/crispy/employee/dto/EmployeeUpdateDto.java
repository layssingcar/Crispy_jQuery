package com.mcp.crispy.employee.dto;

import com.mcp.crispy.common.annotation.NotBlankAndPattern;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeUpdateDto {
    private Integer empNo;

    @NotBlankAndPattern(
            notBlankMessage = "직원 이름을 입력하세요.",
            patternMessage = "직원 이름에는 숫자를 포함할 수 없습니다.",
            pattern = "^[가-힣]+$"
    )
    private String empName;

    @NotBlankAndPattern(
            notBlankMessage = "이메일을 입력해주세요.",
            patternMessage = "올바른 이메일 형식을 입력해주세요.",
            pattern = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$"
    )
    private String empEmail;

    @Pattern(regexp = "^010\\d{4}\\d{4}$|^$", message = "휴대폰 번호 형식을 확인해주세요.")
    private String empPhone;
    private String empSign;

    @NotBlank(message = "우편번호를 확인해주세요.")
    @Size(min = 5, max = 5, message = "우편번호는 정확히 5자리여야 합니다.")
    private String empZip;

    @NotBlank(message = "도로명 주소를 입력하세요.")
    private String empStreet;
    private String empDetail;
    private String empProfile;
    private Position posNo;
    private EmpStatus empStat;
    private Date modifyDt;
    private Integer modifier;
}
