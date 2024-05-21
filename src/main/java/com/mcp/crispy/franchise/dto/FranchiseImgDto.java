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
public class FranchiseImgDto {
    private int frnNo;
    private String frnImg;
    private Date createDt;
    private Date modifyDt;
}
