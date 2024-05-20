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
public class EmployeeRegisterDto {
    private int empNo;
    private String empId;
    private String empPw;
    private String empName;
    private String empEmail;
    private String empPhone;
    private EmpStatus empStat;
    private Date empInDt;
    private Position posNo;
    private int frnNo;
}
