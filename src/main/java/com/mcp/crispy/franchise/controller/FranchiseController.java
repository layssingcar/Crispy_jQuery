package com.mcp.crispy.franchise.controller;

import com.mcp.crispy.franchise.dto.FranchiseDto;
import com.mcp.crispy.franchise.service.FranchiseService;
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
public class FranchiseController {

    private final FranchiseService franchiseService;

//    @IsAdmin
    @GetMapping("/franchise/register")
    public String registerFranchise() {
        return "franchise/franchise-register";
    }

//    @IsAdmin
    @GetMapping("/franchise/owner/register")
    public String ownerRegisterFranchise() {
        return "franchise/franchise-owner-register";
    }

//    @IsOwner
    @GetMapping("/franchise")
    public String getFranchise(Principal principal, Model model) {
        FranchiseDto owner = franchiseService.getFranchise(principal.getName());
        log.info("Franchise owner : {}", owner.getFrnJoinDt());
        model.addAttribute("owner", owner);
        return "franchise/franchise";
    }
    
    @GetMapping("franchise-list")
    public String listFranchise() {
    	return "franchise/franchise-list";
    }
    
    @GetMapping("franchise-map")
    public String mapFranchise() {
    	return "franchise/franchise-map";
    }
}
