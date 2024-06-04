package com.mcp.crispy.employee.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmpAddressUpdateDto {
    private Integer empNo;
    private Integer modifier;

    @NotBlank(message = "우편번호를 확인해주세요.")
    @Size(min = 5, max = 5, message = "우편번호는 정확히 5자리여야 합니다.")
    private String empZip;

    @NotBlank(message = "도로명 주소를 입력하세요.")
    private String empStreet;
    private String empDetail;
}
