package com.mcp.crispy.attendance.controller;

import com.mcp.crispy.attendance.dto.AttendanceDto;
import com.mcp.crispy.attendance.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequiredArgsConstructor
@RequestMapping("/crispy")
public class AttendanceController {
	private final AttendanceService attendanceService;

	@PostMapping(value="/registAtt", produces = "application/json")
	public ResponseEntity<Integer> insertAttendance(@RequestBody AttendanceDto attendanceDto, Principal principal)
	{	
		int insertCount = attendanceService.insertAttendance(attendanceDto);
		return ResponseEntity.ok(insertCount);
	}
	
	@GetMapping(value="/attend", produces="application/json")
	public String getAttList(Model model) {
		model.addAttribute("attenList", attendanceService.getAttList(6));
		return "attendance/attendance";
	}
	
}
