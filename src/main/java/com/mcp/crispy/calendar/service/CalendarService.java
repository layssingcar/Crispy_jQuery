package com.mcp.crispy.calendar.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import com.mcp.crispy.calendar.dto.CalendarDto;
import com.mcp.crispy.calendar.mapper.CalendarMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CalendarService {

	private final CalendarMapper calendarMapper;

	@Transactional(readOnly=true)
	public List<CalendarDto> getTrashList() 
	{
		return calendarMapper.getTrashList();
	}
}
