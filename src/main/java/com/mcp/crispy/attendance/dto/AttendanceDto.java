package com.mcp.crispy.attendance.dto;

import java.sql.Date;
import java.sql.Timestamp;

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
	private Timestamp attInTime;
	private Timestamp attOutTime;
	private String attWorkTime;
	private Date createDt;
	private int creator;
	private Date modifyDt;
	private int modifier;
	private int empNo;
}

