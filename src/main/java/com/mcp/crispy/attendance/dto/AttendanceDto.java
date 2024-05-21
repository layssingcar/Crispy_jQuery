package com.mcp.crispy.attendance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceDto {
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

