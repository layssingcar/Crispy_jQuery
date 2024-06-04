package com.mcp.crispy.franchise.dto;

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
    private String frnName;
    private String empName;
    private String frnOwner;
    private String frnImg;
    private String frnTel;
    private String frnZip;
    private String frnStreet;
    private String frnDetail;
    private String frnStartTime;
    private String frnEndTime;
    private Date modifyDt;
    private Integer modifier;
}
