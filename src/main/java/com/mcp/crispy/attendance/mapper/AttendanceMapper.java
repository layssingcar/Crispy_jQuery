package com.mcp.crispy.attendance.mapper;

import com.mcp.crispy.attendance.dto.AttendanceDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AttendanceMapper {
	List<AttendanceDto> getAttendenceList(int boardNo);
	int insertAttendance(AttendanceDto attendanceDto);
}
