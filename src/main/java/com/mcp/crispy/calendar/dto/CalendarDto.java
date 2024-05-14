package com.mcp.crispy.calendar.dto;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalendarDto {
	private int commentNo;
	private String commentContent;
	private Timestamp cmtCreateDt;
	private Timestamp cmtModifyDt;
}
