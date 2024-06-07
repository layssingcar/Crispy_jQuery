package com.mcp.crispy.calendar.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.mcp.crispy.calendar.dto.CalendarDto;

@Mapper
public interface CalendarMapper {
	List<CalendarDto> getTrashList();	
}
