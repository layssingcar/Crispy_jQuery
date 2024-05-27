package com.mcp.crispy.schedule.service;

import com.mcp.crispy.attendance.dto.AttendanceDto;
import com.mcp.crispy.attendance.mapper.AttendanceMapper;
import com.mcp.crispy.schedule.mapper.ScheduleMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleService {

	private final ScheduleMapper scheduleMapper;

}
