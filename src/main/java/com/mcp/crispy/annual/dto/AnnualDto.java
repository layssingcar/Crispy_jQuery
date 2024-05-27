package com.mcp.crispy.annual.dto;

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
public class AnnualDto {
	private int annNo;
	private int annCtNo;
	private Timestamp annStartTime;
	private Timestamp annEndTime;
	private Date createDt;
	private int creator;
	private Date modifyDt;
	private int modifier;
	private int empNo;
}

