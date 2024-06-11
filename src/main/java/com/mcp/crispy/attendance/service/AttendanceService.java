package com.mcp.crispy.attendance.service;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
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
		List<AttendanceDto> attenList = attendanceMapper.getAttListByInsert();
		AttendanceDto attend = null;
		for(int i = 0; i < attenList.size(); i++) {
			if(attenList.get(i).getCreateDt().equals(attendanceDto.getCreateDt())) {
				attend = AttendanceDto.builder()
										.attInTime(attendanceDto.getAttInTime())
										.attOutTime(attendanceDto.getAttOutTime())
										.attWorkTime(attendanceDto.getAttWorkTime())
										.createDt(attendanceDto.getCreateDt())
										.empNo(attendanceDto.getEmpNo())
										.build();
				break;
			}
		}
		
		if(attend == null)
			return attendanceMapper.insertAttendance(attendanceDto);
		else
			return attendanceMapper.updateAttendance(attend);
		
	}
	
	@Transactional(readOnly = true)
	public List<AttendanceDto> getAttList(Map<String, Object> params) {
	    List<AttendanceDto> attenList = attendanceMapper.getAttList(params);
	    List<AttendanceDto> annList = attendanceMapper.getAnnList(params);
	    List<AttendanceDto> totalList = new ArrayList<>();
	    Map<Date, AttendanceDto> annualMap = new HashMap<>();
	    
	    for (AttendanceDto annual : annList) {
	        annualMap.put(annual.getCreateDt(), annual);
	    }
	    
	    for (AttendanceDto atten : attenList) {
	        if (annualMap.containsKey(atten.getCreateDt())) {					
	            AttendanceDto annual = annualMap.get(atten.getCreateDt());		
	            if (annual.getCategory() == 1 || annual.getCategory() == 2) {
	                annual.setAttInTime(atten.getAttInTime());
	                annual.setAttOutTime(atten.getAttOutTime());
	                annual.setAttWorkTime(atten.getAttWorkTime());
	            }
	            totalList.add(annual);
	            annualMap.remove(atten.getCreateDt());
	        } else {
	            totalList.add(atten);
	        }
	    }
	    totalList.addAll(annualMap.values());
	    return totalList;
	}
}
