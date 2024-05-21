package com.mcp.crispy.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class EmpAddressDto {
    private int empNo;
    private String empZip;
    private String empStreet;
    private String empDetail;
    private Date createDt;
    private Date modifyDt;
}
