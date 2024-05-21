package com.mcp.crispy.attendence.dto;

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
public class AttendenceDto {
	private int attNo;
	private Timestamp attInDt;
	private Timestamp attOutDt;
	private String attWorkTime;
	private Date createDt;
	private int creator;
	private Date modifyDt;
	private int modifier;
	private int empNo;
}

