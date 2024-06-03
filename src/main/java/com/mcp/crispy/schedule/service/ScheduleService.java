package com.mcp.crispy.schedule.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mcp.crispy.schedule.dto.ScheduleDto;
import com.mcp.crispy.schedule.mapper.ScheduleMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScheduleService {

	private final ScheduleMapper scheduleMapper;
	
	@Transactional
	public int insertSchedule(ScheduleDto scheduleDto)
	{
		return scheduleMapper.insertSchedule(scheduleDto);
	}
	
	@Transactional(readOnly = true)
	public List<ScheduleDto> getScheList() {
		List<ScheduleDto> attenList = scheduleMapper.getScheList(); 
		
		return attenList;
	}
	
	@Transactional(readOnly = true)
	public ScheduleDto getScheById(String id) {
		return scheduleMapper.getScheById(id);
	}
	
	@Transactional
	public int modifySchedule(ScheduleDto scheduleDto) {
		return scheduleMapper.modifySchedule(scheduleDto);
	}
}
