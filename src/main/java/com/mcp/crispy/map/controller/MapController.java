package com.mcp.crispy.map.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/crispy")
@RequiredArgsConstructor
public class MapController {

    /**
     * 박종구 - 가맹점 목록 및 정보조회 (카카오 맵)
     * 2024-05-31
     * @param
     * @return forward (franchise/franchise-map.html)
     */
    @GetMapping("franchise-map")
    public String mapFranchise() {
        return "franchise/franchise-map";
    }
}
