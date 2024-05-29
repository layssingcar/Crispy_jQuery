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
	private String annId;
	private int annCtNo;
	private String annTitle;
	private String annContent;
	private int annTotal;
	private String annStartTime;
	private String annEndTime;
	private Date createDt;
	private int creator;
	private Date modifyDt;
	private int modifier;
	private int empNo;
}

