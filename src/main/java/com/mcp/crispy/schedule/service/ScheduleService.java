package com.mcp.crispy.schedule.service;

import com.mcp.crispy.employee.dto.EmployeeDto;
import com.mcp.crispy.employee.service.EmployeeService;
import com.mcp.crispy.schedule.dto.ScheduleDto;
import com.mcp.crispy.schedule.mapper.ScheduleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleService {

	private final ScheduleMapper scheduleMapper;
	private final EmployeeService employeeService;
	
	@Transactional
	public int insertSchedule(ScheduleDto scheduleDto)
	{
		EmployeeDto emp = employeeService.getEmployeeDetailsByEmpNo(scheduleDto.getEmpNo());
		scheduleDto.setFrnNo(emp.getFrnNo());
		return scheduleMapper.insertSchedule(scheduleDto);
	}
	
	@Transactional(readOnly = true)
	public List<ScheduleDto> getScheList(int empNo) {
		EmployeeDto emp = employeeService.getEmployeeDetailsByEmpNo(empNo);
		List<ScheduleDto> attenList = scheduleMapper.getScheList(emp.getFrnNo()); 
		return attenList;
	}
	
	@Transactional(readOnly = true)
	public ScheduleDto getScheById(String id) {
		return scheduleMapper.getScheById(id);
	}
	
	@Transactional
	public int modifySchedule(ScheduleDto scheduleDto) {
		return scheduleMapper.modifySchedule(scheduleDto);
	}
	@Transactional
	public int deleteSchedule(ScheduleDto scheduleDto) {
		return scheduleMapper.deleteSchedule(scheduleDto);
	}
	@Transactional
	public int completeDeleteSchedule(ScheduleDto scheduleDto) {
		return scheduleMapper.completeDeleteSchedule(scheduleDto);
	}
}
