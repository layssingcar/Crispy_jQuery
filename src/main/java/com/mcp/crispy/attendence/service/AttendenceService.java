package com.mcp.crispy.attendence.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import com.mcp.crispy.attendence.dto.AttendenceDto;
import com.mcp.crispy.attendence.mapper.AttendenceMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AttendenceService {

	private final AttendenceMapper attendenceMapper;

	@Transactional(readOnly=true)
	public List<AttendenceDto> getAttendenceList(Model model, int attNo) 
	{
		return attendenceMapper.getAttendenceList(attNo);
	}

	@Transactional
	public int insertAttendence(AttendenceDto attendenceDto)
	{
		return attendenceMapper.insertAttendence(attendenceDto);
	}
}
