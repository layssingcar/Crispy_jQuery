package com.mcp.crispy.attendance.mapper;

import com.mcp.crispy.attendance.dto.AttendanceDto;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface AttendanceMapper {
	int insertAttendance(AttendanceDto attendanceDto);
	List<AttendanceDto> getAttList(Map<String, Object> map);
}
