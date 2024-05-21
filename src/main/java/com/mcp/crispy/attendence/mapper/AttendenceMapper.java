package com.mcp.crispy.attendence.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import com.mcp.crispy.attendence.dto.AttendenceDto;

@Mapper
public interface AttendenceMapper {
	List<AttendenceDto> getAttendenceList(int boardNo);	
	int insertAttendence(AttendenceDto attendenceDto);
}
