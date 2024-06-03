package com.mcp.crispy.map.controller;

import com.mcp.crispy.map.dto.MapDto;
import com.mcp.crispy.map.service.MapService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/crispy")
@RequiredArgsConstructor
public class MapController {

    private final MapService mapService;

    /**
     * 박종구 - Kakao Map View
     * 2024-05-31
     * @return forward (franchise/franchise-map.html)
     */
    @GetMapping("/franchise-map")
    public String franchiseMapView() {
        return "franchise/franchise-map";
    }

    /**
     * 박종구 - 가맹점 목록 및 정보조회 (카카오 맵)
     * 2024-05-31
     * @param model
     */
    @ResponseBody
    @GetMapping(value = "/frnMapApi", produces = "application/json")
    public Map<String, Object> franchiseMap(Model model) {
        List<MapDto> mapList = mapService.getMapList();
        model.addAttribute("mapList", mapList);
        return Map.of("mapList", mapList);
    }
}
