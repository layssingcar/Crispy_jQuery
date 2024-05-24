package com.mcp.crispy.attendance.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mcp.crispy.attendance.dto.AttendanceDto;
import com.mcp.crispy.attendance.mapper.AttendanceMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AttendanceService {

	private final AttendanceMapper attendanceMapper;

	@Transactional
	public int insertAttendance(AttendanceDto attendanceDto)
	{
		return attendanceMapper.insertAttendance(attendanceDto);
	}
	
	@Transactional(readOnly = true)
	public List<AttendanceDto> getAttList(Map<String, Object> map) {
		return attendanceMapper.getAttList(map);
	}
}
