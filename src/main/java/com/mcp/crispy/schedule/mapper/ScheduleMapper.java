package com.mcp.crispy.schedule.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.mcp.crispy.schedule.dto.ScheduleDto;

@Mapper
public interface ScheduleMapper {
	int insertSchedule(ScheduleDto scheduleDto);
	List<ScheduleDto> getScheList();
	ScheduleDto getScheById(String id);
	int modifySchedule(ScheduleDto scheduleDto);
}
