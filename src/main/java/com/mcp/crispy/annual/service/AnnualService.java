package com.mcp.crispy.annual.service;

import com.mcp.crispy.annual.dto.AnnualDto;
import com.mcp.crispy.annual.mapper.AnnualMapper;
import com.mcp.crispy.attendance.dto.AttendanceDto;
import com.mcp.crispy.attendance.mapper.AttendanceMapper;
import com.mcp.crispy.schedule.dto.ScheduleDto;
import com.mcp.crispy.schedule.mapper.ScheduleMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnnualService {

	private final AnnualMapper annualMapper;
	
	@Transactional
	public int insertAnnual(AnnualDto annualDto)
	{
		return annualMapper.insertAnnual(annualDto);
	}
	
	@Transactional(readOnly = true)
	public List<AnnualDto> getAnnList() {
		List<AnnualDto> annList = annualMapper.getAnnList(); 
		
		return annList;
	}
}
