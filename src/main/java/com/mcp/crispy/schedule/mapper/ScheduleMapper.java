package com.mcp.crispy.schedule.mapper;

import com.mcp.crispy.schedule.dto.ScheduleDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ScheduleMapper {
	int insertSchedule(ScheduleDto scheduleDto);
	List<ScheduleDto> getScheList(Map<String, Object> params);
	ScheduleDto getScheById(String id);
	int modifySchedule(ScheduleDto scheduleDto);
	int deleteSchedule(ScheduleDto scheduleDto);
	int completeDeleteSchedule(ScheduleDto scheduleDto);
	int revertSchedule(ScheduleDto scheduleDto);
	int getCountSche();
}
