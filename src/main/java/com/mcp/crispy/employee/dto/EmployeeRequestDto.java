package com.mcp.crispy.employee.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeRequestDto {
    private int empNo;
    private String empId;
    private String empPw;
    private String empName;
    private String empEmail;
    private String empPhone;
    private String empZip;
    private String empStreet;
    private String empDetail;
    private int frnNo;
    private String frnName;
    private String posName;
    private EmpStatus empStat;
    private Position posNo;

    @JsonProperty("empStat")
    public String getEmpStatDesc() {
        return empStat != null ? empStat.getDescription() : null;
    }
}
