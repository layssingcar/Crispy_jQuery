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
public class EmployeeUpdateDto {
    private Integer empNo;
    private String empPhone;
    private String empName;
    private Position posNo;
    private EmpStatus empStat;
    private Date modifyDt;
    private Integer modifier;
}
