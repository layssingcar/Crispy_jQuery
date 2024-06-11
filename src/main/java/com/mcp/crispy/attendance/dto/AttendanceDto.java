package com.mcp.crispy.attendance.dto;

import java.sql.Date;
import java.sql.Timestamp;

import com.mcp.crispy.employee.dto.EmployeeDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceDto {
	private int attNo;
	private String attInTime;
	private String attOutTime;
	private String attWorkTime;
	private Date createDt;
	private int creator;
	private Date modifyDt;
	private int modifier;
	private int empNo;
	private int annCtNo;
	private int category;
}

