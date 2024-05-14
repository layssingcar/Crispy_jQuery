package com.mcp.crispy;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/crispy")
public class MainController {

	@GetMapping("/main")
	public String Main() {
		return "index";
	}

	@GetMapping("/tables")
	public String Board() {
		return "pages/tables";
	}

	@GetMapping("/tables2")
	public String Board2() {
		return "pages/tables2";
	}

	@GetMapping("/billing")
	public String Billing() {
		return "pages/billing";
	}

	@GetMapping("/virtual-reality")
	public String Virtualreality() {
		return "pages/virtual-reality";
	}

	@GetMapping("/rtl")
	public String Rtl() {
		return "pages/rtl";
	}

	@GetMapping("/profile")
	public String Profile() {
		return "pages/profile";
	}

	@GetMapping("/sign-in")
	public String SignIn() {
		return "pages/sign-in";
	}

	@GetMapping("/sign-up")
	public String SignUp() {
		return "pages/sign-up";
	}
	
	@GetMapping("/calendar")
	public String CalendarTest() {
		return "calendar/calendar";
	}
}
