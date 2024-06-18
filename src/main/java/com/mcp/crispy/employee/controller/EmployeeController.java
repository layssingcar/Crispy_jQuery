package com.mcp.crispy.employee.controller;

import com.mcp.crispy.common.config.CrispyUserDetailsService;
import com.mcp.crispy.common.utils.JwtUtil;
import com.mcp.crispy.employee.dto.EmployeeDto;
import com.mcp.crispy.employee.dto.FindEmpId;
import com.mcp.crispy.employee.dto.FindEmployeeDto;
import com.mcp.crispy.employee.service.EmployeeService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequestMapping("/crispy/employee")
@RequiredArgsConstructor
public class EmployeeController {

	private final EmployeeService employeeService;
	private final CrispyUserDetailsService crispyUserDetailsService;
	private final JwtUtil jwtUtil;

	/**
	 * 아이디 찾기 페이지
	 * 배영욱 (24 05. 14)
	 * @param model model
	 * @return forward (find-emp-id)
	 */
	@GetMapping("/findEmpId")
	public String findUsername(Model model) {
		model.addAttribute("findEmployeeDto", new FindEmployeeDto());
		log.info("findEmployeeDto: {}", new FindEmployeeDto());
		return "employee/find-emp-id";
	}

	/**
	 * 아이디 찾기 POST
	 * 배영욱 (24. 05. 21)
	 * @param findEmpId 아이디 찾기 DTO
	 * @param result 유효성 검사 결과
	 * @param model model
	 * @param ra 리다이렉트 속성
	 * @return forward (find-emp-id-result)
	 */
	@PostMapping("/findEmpId")
	public String findUsernamePost(@Valid @ModelAttribute FindEmpId findEmpId, BindingResult result,
								   Model model, RedirectAttributes ra) {
		if (result.hasErrors()) {
			log.info("Validation errors: {}", result.getAllErrors());
			model.addAttribute("findEmployeeDto", findEmpId);
			return "employee/find-emp-id";
		}

		FindEmployeeDto findEmp = employeeService.getEmpEmail(findEmpId.getEmpEmail(), findEmpId.getEmpName());
		if (findEmp != null) {
			ra.addFlashAttribute("findEmp", findEmp);
			log.info("empName: {}", findEmp.getEmpId());
			log.info("empCreateDt: {}", findEmp.getCreateDtAsLocalDate());
		} else {
			ra.addFlashAttribute("error", "해당 이메일로 가입된 아이디가 없습니다.");
		}
		return "redirect:/crispy/employee/findEmpId/result";
	}

	/**
	 * 아이디 찾기 결과 페이지
	 * 배영욱 (24. 05. 21)
	 * @return forward (find-emp-id-result)
	 */
	@GetMapping("/findEmpId/result")
	public String findUsernameResult() {
		return "employee/find-emp-id-result";
	}

	/**
	 * 비밀번호 찾기 페이지
	 * 배영욱 (24. 05. 22)
	 * @param model model
	 * @return forward (find-emp-pw)
	 */
	@GetMapping("/findEmpPw")
	public String findPassword(Model model) {
		model.addAttribute("findEmployeeDto", new FindEmployeeDto());
		return "employee/find-emp-pw";
	}

	/**
	 * 비밀번호 찾기 POST
	 * 배영욱 (24. 05. 22)
	 * @param findEmployeeDto 직원 정보 찾기 DTO
	 * @param result 유효성 검사 결과
	 * @param ra 리다이렉트 속성
	 * @param response 쿠키 추가
	 * @param model model
	 * @return forward (change-emp-pw)
	 */
	@PostMapping("/findEmpPw")
	public String findPasswordPost(@Valid @ModelAttribute FindEmployeeDto findEmployeeDto, BindingResult result,
								   RedirectAttributes ra, HttpServletResponse response, Model model) {

		if (result.hasErrors()) {
			model.addAttribute("findEmployeeDto", findEmployeeDto);
			return "employee/find-emp-pw";
		}

		FindEmployeeDto findEmp = employeeService.getEmpEmail(findEmployeeDto.getEmpEmail(), findEmployeeDto.getEmpName());
		if (findEmp != null) {
			// 사용자 인증
			UserDetails userDetails = crispyUserDetailsService.converToUserDetails(findEmp);
			String token = jwtUtil.createAccessToken(userDetails);

			Cookie cookie = new Cookie("accessToken", token);
			cookie.setHttpOnly(true);
			cookie.setPath("/"); // 모든 경로에서 접근 가능
			response.addCookie(cookie);

			return "redirect:/crispy/employee/changeEmpPw";
		} else {
			ra.addFlashAttribute("error", "일치하는 회원 정보가 없습니다.");
			return "redirect:/crispy/employee/findEmpPw";
		}
	}

	/**
	 * 비밀번호 변경 페이지
	 * 배영욱 (24. 06. 06)
	 * @param token JWT 토큰
	 * @param session http 세션
	 * @param model model
	 * @return forward (change-emp-pw)
	 */
	@GetMapping("/changeEmpPw")
	public String changePassword(@CookieValue(value = "accessToken", required = false) String token,
								 HttpSession session, Model model) {
		log.info("changePassword method called");
		log.info("JWT Token: {}", token);

		if (token != null && jwtUtil.validateToken(token)) {
			UserDetails userDetails = jwtUtil.getUserDetailsFromToken(token);
			log.info("changePassword userDetails: {}", userDetails);
			session.setAttribute("empId", userDetails.getUsername());
			model.addAttribute("empId", userDetails.getUsername());
			return "employee/change-emp-pw";
		} else {
			return "redirect:/crispy/employee/findEmpPw?error=invalid_token";
		}
	}

	/**
	 * 마이페이지
	 * 배영욱 (24. 05. 20)
	 * @param model model
	 * @return forward (employee-profile)
	 */
	// 직원 혹은 관리자 개인이 들어가는 마이 페이지
	@GetMapping("/profile")
	public String getEmployee(Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		boolean isAdmin = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

		if (isAdmin) {
			return "redirect:/error/403";
		}

		EmployeeDto employee = employeeService.getEmployeeName(authentication.getName());
		log.info("address : {} {} {}", employee.getEmpZip(), employee.getEmpStreet(), employee.getEmpDetail());
		log.info("empSign : {}", employee.getEmpSign());
		model.addAttribute("employee", employee);
		return "employee/employee-profile";
	}

}