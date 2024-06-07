package com.mcp.crispy.schedule.controller;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mcp.crispy.schedule.dto.ScheduleDto;
import com.mcp.crispy.schedule.service.ScheduleService;

import lombok.RequiredArgsConstructor;


@Controller
@RequiredArgsConstructor
@RequestMapping("/crispy")
public class ScheduleController {
	private final ScheduleService scheduleService;
	
	@PostMapping(value="/registSche", produces = "application/json")
	public ResponseEntity<Integer> insertSchedule(@RequestBody ScheduleDto scheduleeDto, Principal principal)
	{	
		int insertCount = scheduleService.insertSchedule(scheduleeDto);
		return ResponseEntity.ok(insertCount);
	}
	
	@ResponseBody
	@GetMapping(value="/getScheList", produces="application/json")
	public List<ScheduleDto> getScheList() {
		return scheduleService.getScheList();
	}
	
	@ResponseBody
	@GetMapping(value = "/getScheById", produces = "application/json")
	public ScheduleDto getScheById(@RequestParam("scheId") String scheId, Model model) {
		return scheduleService.getScheById(scheId);
	}
	
	@PostMapping(value="/modifySche", produces = "application/json")
	public ResponseEntity<Integer> modifySchedule(@RequestBody ScheduleDto scheduleDto, Principal principal){
		int modifyCount = scheduleService.modifySchedule(scheduleDto);
		return ResponseEntity.ok(modifyCount);
	}
	
	@PostMapping(value="/deleteSche", produces = "application/json")
	public ResponseEntity<Integer> deleteSchedule(@RequestBody ScheduleDto scheduleDto, Principal principal){

		int deleteCount = scheduleService.deleteSchedule(scheduleDto);
		return ResponseEntity.ok(deleteCount);
	}
	
	@DeleteMapping(value="/completeDeleteSche", produces = "application/json")
	public ResponseEntity<Integer> completeDeleteSchedule(@RequestBody ScheduleDto scheduleDto, Principal principal){
		int deleteCount = scheduleService.completeDeleteSchedule(scheduleDto);
		return ResponseEntity.ok(deleteCount);
	}
	
}
