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
	private String trashType;
	private String trashTitle;
	private String trashContent;
	private String trashDt;
}
