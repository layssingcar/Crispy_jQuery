package com.mcp.crispy;

import com.mcp.crispy.admin.dto.AdminDto;
import com.mcp.crispy.admin.service.AdminService;
import com.mcp.crispy.employee.dto.EmployeeDto;
import com.mcp.crispy.employee.service.EmployeeService;
import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

	private final EmployeeService employeeService;
	private final AdminService adminService;

	@GetMapping("/main")
	public String index(Principal principal, Model model) {
		if (principal != null) {
			String username = principal.getName();
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

			boolean isAdmin = authentication.getAuthorities().stream()
					.anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

			if (isAdmin) {
				AdminDto admin = adminService.getAdmin(username);
				model.addAttribute("isAdmin", admin);
				log.info("Admin: {}", admin);
			} else {
				EmployeeDto loggedEmp = employeeService.getEmployeeName(username);
				model.addAttribute("loggedEmp", loggedEmp);
				log.info("loggedEmp: {}", loggedEmp);
			}
		}
		return "index";
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
}
