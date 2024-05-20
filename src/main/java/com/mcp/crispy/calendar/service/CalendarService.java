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

	private final CalendarMapper commentMapper;

	@Transactional(readOnly=true)
	public List<CalendarDto> getCalendarList(Model model, int boardNo) 
	{
		List<CalendarDto> cmtList = commentMapper.getCalendarList(boardNo);
		model.addAttribute("commentList", cmtList);
		return commentMapper.getCalendarList(boardNo);
	}

	@Transactional
	public int insertCalendar()
	{
		return 0;
	}
	
	@Transactional
	public int deleteCalendar() 
	{
		return 0;
	}

	@Transactional
	public int modifyCalendar() 
	{	
		return 0;
	}
}
