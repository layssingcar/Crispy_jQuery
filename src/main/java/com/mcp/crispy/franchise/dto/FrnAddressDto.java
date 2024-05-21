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
public class FrnAddressDto {
    private int frnNo;
    private int empNo;
    private int modifier;
    private String frnZip;
    private String frnStreet;
    private String frnDetail;
    private Date createDt;
    private Date modifyDt;
}
