package com.mcp.crispy.franchise.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/crispy")
public class FranchiseController {

    @GetMapping("/franchise/register")
    public String registerFranchise() {
        return "franchise/franchise-register";
    }

    @GetMapping("/franchise/owner/register")
    public String ownerRegisterFranchise() {
        return "franchise/franchise-owner-register";
    }

    @GetMapping("/franchise")
    public String getFranchise() {
        return "franchise/franchise";
    }
}
