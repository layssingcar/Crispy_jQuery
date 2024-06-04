package com.mcp.crispy.franchise.dto;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FrnTelUpdateDto {
    @Pattern(regexp = "^\\d{2,3}\\d{3,4}\\d{4}$|^$",
             message = "휴대폰 번호 형식을 확인해주세요.")
    private String FrnTel;
    private Integer modifier;
    private Integer frnNo;
}
