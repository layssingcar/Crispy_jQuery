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
    private String empName;
    private String empEmail;
    private String empPhone;
    private String empSign;
    private String empZip;
    private String empStreet;
    private String empDetail;
    private String empProfile;
    private Position posNo;
    private EmpStatus empStat;
    private Date modifyDt;
    private Integer modifier;
}
