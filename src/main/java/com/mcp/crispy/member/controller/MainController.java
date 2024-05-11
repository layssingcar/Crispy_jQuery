package com.mcp.crispy.member.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/itrn")
public class MainController {
	
	@GetMapping("/dashboard")
	public String Main() {
		return "pages/dashboard";
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
}
