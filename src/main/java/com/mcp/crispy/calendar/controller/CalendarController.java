package com.mcp.crispy.calendar.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mcp.crispy.calendar.service.CalendarService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/crispy")
public class CalendarController {
	private final CalendarService calendarService;

	
	@GetMapping("/trash")
	public String getTrashList(Model model) {
		model.addAttribute("trashList", calendarService.getTrashList());
		return "trash/trash";
	}
}
