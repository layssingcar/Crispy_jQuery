package com.mcp.crispy.schedule.dto;

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
public class ScheduleDto {
	private int scheNo;
	private String scheId;
	private int frnNo;
	private int scheDiv;
	private String scheTitle;
	private String scheContent;
	private String scheStartTime;
	private String scheEndTime;
	private Date createDt;
	private int creator;
	private Date modifyDt;
	private int modifier;
	private int scheStat;
	private int empNo;
}

