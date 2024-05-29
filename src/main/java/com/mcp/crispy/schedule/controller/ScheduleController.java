package com.mcp.crispy.schedule.controller;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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
	
	@GetMapping("/sche")
	public String getScheList(Model model) {
		model.addAttribute("scheList", scheduleService.getScheList());
		return "schedule/schedule";
	}
	
	@ResponseBody
	@GetMapping(value = "/getScheById", produces = "application/json")
	public ScheduleDto getScheById(@RequestParam("scheId") String scheId, Model model) {
		return scheduleService.getScheById(scheId);
	}
	
}
