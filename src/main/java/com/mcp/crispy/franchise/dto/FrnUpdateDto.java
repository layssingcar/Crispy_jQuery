package com.mcp.crispy.franchise.dto;

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
public class FrnUpdateDto {
    private Integer frnNo;
    private Integer posNo;
    private Integer empNo;
    @NotBlankAndPattern(
            notBlankMessage = "가맹점 이름을 입력하세요.",
            patternMessage = "가맹점 이름에는 숫자를 포함할 수 없습니다.",
            pattern = "^[가-힣a-zA-Z\\s]+$"
    )
    private String frnName;
    private String empName;

    @NotBlankAndPattern(
            notBlankMessage = "대표자 이름을 입력하세요.",
            patternMessage = "대표자 이름에는 숫자를 포함할 수 없습니다.",
            pattern = "^[가-힣]+$"
    )
    private String frnOwner;
    private String frnImg;

    @Pattern(regexp = "^\\d{2,3}\\d{3,4}\\d{4}$", message = "올바른 전화번호 형식을 입력해주세요.")
    private String frnTel;

    @NotBlank(message = "우편번호를 확인해주세요.")
    @Size(min = 5, max = 5, message = "우편번호는 정확히 5자리여야 합니다.")
    private String frnZip;

    @NotBlank(message = "도로명 주소를 입력하세요.")
    private String frnStreet;

    private String frnDetail;
    private String frnGu;
    private String frnStartTime;
    private String frnEndTime;
    private Date modifyDt;
    private Integer modifier;
}
