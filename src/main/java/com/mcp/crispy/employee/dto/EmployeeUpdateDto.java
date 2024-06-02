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
public class EmployeeUpdateDto {
    private Integer empNo;
    private String empName;

    @NotBlankAndPattern(
            notBlankMessage = "이메일을 입력해주세요.",
            patternMessage = "올바른 이메일 형식을 입력해주세요.",
            pattern = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$"
    )
    private String empEmail;

    @Pattern(regexp = "^010\\d{3,4}\\d{4}$|^$", message = "휴대폰 번호 형식을 확인해주세요.")
    private String empPhone;
    private String empSign;
    private String empZip;
    private String empStreet;
    private String empDetail;
    private String empProfile;
    private Position posNo;
    private EmpStatus empStat;
    private Date modifyDt;
    private Integer modifier;
}
