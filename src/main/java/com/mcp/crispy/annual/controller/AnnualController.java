package com.mcp.crispy.annual.controller;

import com.mcp.crispy.annual.service.AnnualService;
import com.mcp.crispy.attendance.dto.AttendanceDto;
import com.mcp.crispy.attendance.service.AttendanceService;
import com.mcp.crispy.schedule.service.ScheduleService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/crispy")
public class AnnualController {
	private final AnnualService annualService;
	
	
}
