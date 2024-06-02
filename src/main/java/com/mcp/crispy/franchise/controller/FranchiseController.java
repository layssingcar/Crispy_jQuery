package com.mcp.crispy.franchise.controller;

import com.mcp.crispy.common.annotation.IsAdmin;
import com.mcp.crispy.common.annotation.IsAdminOrIsOwner;
import com.mcp.crispy.franchise.dto.FranchiseDto;
import com.mcp.crispy.franchise.service.FranchiseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/crispy")
@RequiredArgsConstructor
public class FranchiseController {

    private final FranchiseService franchiseService;

    /**
     * 가맹점 등록 페이지
     * 관리자만 접근 가능
     * 배영욱 (24. 05. 15)
     * @return forward (franchise-register.html)
     */
    @IsAdmin
    @GetMapping("/franchise/register")
    public String registerFranchise() {
        return "franchise/franchise-register";
    }

    /**
     * 점주 등록 페이지
     * 관리자만 접근 가능
     * 배영욱 (24. 05. 15)
     * @return forward (franchise-owner-register.html)
     */
    @IsAdmin
    @GetMapping("/franchise/owner/register")
    public String ownerRegisterFranchise() {
        return "franchise/franchise-owner-register";
    }


    /**
     * 가맹점 정보 조회
     * 관리자 또는 점주만 접근 가능
     * 배영욱 (24. 05. 23)
     * @param principal 인증 정보
     * @param model 모델 객체
     * @return forward (franchise.html)
     */
    @IsAdminOrIsOwner
    @GetMapping("/franchise")
    public String getFranchise(Principal principal, Model model) {
        if(principal != null) {
            FranchiseDto owner = franchiseService.getFranchise(principal.getName());
            model.addAttribute("owner", owner);
            return "franchise/franchise";
        } else {
            return "redirect:/crispy/login";
        }
    }

    @IsAdmin
    @GetMapping("/franchise-list")
    public String listFranchise(Model model, FranchiseDto franchiseDto) {
        List<FranchiseDto> franchiseList = franchiseService.getFranchiseList();
        model.addAttribute("franchiseList", franchiseList);
    	return "franchise/franchise-list";
    }
}
