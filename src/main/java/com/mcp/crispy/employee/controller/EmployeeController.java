package com.mcp.crispy.employee.controller;

import com.mcp.crispy.employee.dto.EmployeeDto;
import com.mcp.crispy.employee.dto.FindEmployeeDto;
import com.mcp.crispy.employee.service.EmployeeService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Slf4j
@Controller
@RequestMapping("/crispy/employee")
@RequiredArgsConstructor
public class EmployeeController {

	private final EmployeeService employeeService;

	@GetMapping("/findEmpId")
	public String findUsername() {
		return "employee/find-emp-id";
	}

	@PostMapping("/findEmpId")
	public String findUsernamePost(@ModelAttribute FindEmployeeDto findEmployeeDto, BindingResult result,
								   RedirectAttributes ra) {
		if (result.hasErrors()) {
			return "employee/find-emp-id";
		}

		FindEmployeeDto findEmp = employeeService.getEmpEmail(findEmployeeDto.getEmpEmail(), findEmployeeDto.getEmpName());
		if (findEmp != null) {
			ra.addFlashAttribute("findEmpName", findEmp.getEmpId());
			log.info("empName: {}", findEmp.getEmpId());
		} else {
			ra.addFlashAttribute("error", "해당 이메일로 가입된 아이디가 없습니다.");
		}
		return "redirect:/crispy/employee/findEmpIdResult";
	}

	@GetMapping("/findEmpId/result")
	public String findUsernameResult() {
		return "employee/find-emp-id-result";
	}

	@GetMapping("/findEmpPw")
	public String findPassword() {
		return "employee/find-emp-pw";
	}

	@GetMapping("/changeEmpPw")
	public String changePassword(HttpSession session, Principal principal, Model model) {
		if(principal != null) {
			EmployeeDto employee = employeeService.getEmployeeName(principal.getName());
			model.addAttribute("empId", employee.getEmpId());
		} else if (session.getAttribute("empId") != null) {
			String findEmpId = (String) session.getAttribute("empId");
			model.addAttribute("empId", findEmpId);
		}
		return "employee/change-emp-pw";
	}

	// 직원 혹은 관리자 개인이 들어가는 마이 페이지
	@GetMapping("/profile")
	public String getEmployee(Principal principal, Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		boolean isAdmin = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

		if (isAdmin) {
			return "redirect:/error/403";
		}


		EmployeeDto employee = employeeService.getEmployeeName(principal.getName());
		log.info("address : {} {} {}", employee.getEmpZip(), employee.getEmpStreet(), employee.getEmpDetail());
		log.info("empSign : {}", employee.getEmpSign());
		model.addAttribute("employee", employee);
		return "employee/employee-profile";
	}

}