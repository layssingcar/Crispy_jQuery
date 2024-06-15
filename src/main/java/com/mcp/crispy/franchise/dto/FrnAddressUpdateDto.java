package com.mcp.crispy.franchise.dto;


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
public class FrnAddressUpdateDto {
    private Integer frnNo;
    private Integer modifier;

    @NotBlank(message = "우편번호를 확인해주세요.")
    @Size(min = 5, max = 5, message = "우편번호는 정확히 5자리여야 합니다.")
    private String frnZip;

    @NotBlank(message = "도로명 주소를 입력하세요.")
    private String frnStreet;
    private String frnDetail;
    private String frnGu;
}
