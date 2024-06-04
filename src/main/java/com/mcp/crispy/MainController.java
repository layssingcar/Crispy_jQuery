package com.mcp.crispy;

import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Slf4j
@Controller
@RequestMapping("/crispy")
@RequiredArgsConstructor
public class MainController {

	@GetMapping("/main")
	public String index(Principal principal, Model model) {
		if (principal != null) {
			return "index";
		} else {
			return "redirect:/crispy/login";
		}
	}

	@PermitAll
	@GetMapping("/signup")
	public String signup() {
		return "signup";
	}

	@GetMapping("/login")
	public String login() {
		return "login";
	}
	@GetMapping("/schedule")
	public String ScheduleTest() {
		return "schedule/schedule";
	}

	@GetMapping("/calendar")
	public String calendarTest() {
		return "calendar/calendar";
	}
	
	@GetMapping("/trash")
	public String trashTest() {
		return "trash/trash";
	}
}
