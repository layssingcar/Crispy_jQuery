package com.mcp.crispy.attendance.service;

import com.mcp.crispy.attendance.dto.AttendanceDto;
import com.mcp.crispy.attendance.mapper.AttendanceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceService {

	private final AttendanceMapper attendanceMapper;

	@Transactional(readOnly=true)
	public List<AttendanceDto> getAttendenceList(Model model, int attNo)
	{
		return attendanceMapper.getAttendenceList(attNo);
	}

	@Transactional
	public int insertAttendance(AttendanceDto attendanceDto)
	{
		return attendanceMapper.insertAttendance(attendanceDto);
	}
}
