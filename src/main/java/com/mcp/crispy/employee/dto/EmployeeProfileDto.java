package com.mcp.crispy.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeProfileDto {
    private int empNo;
    private String empProfile;
    private Date createDt;
    private Date modifyDt;
}
