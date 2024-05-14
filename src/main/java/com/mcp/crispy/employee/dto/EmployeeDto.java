package com.mcp.crispy.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class EmployeeDto {
    private int empNo;
    private String empId;
    private String empPw;
    private String empName;
    private String empEmail;
    private String empPhone;

}
