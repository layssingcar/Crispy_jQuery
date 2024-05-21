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
public class FindEmployeeDto {
    private String empId;
    private String empName;
    private String empPw;
    private String empEmail;
    private Date createDt;
}
