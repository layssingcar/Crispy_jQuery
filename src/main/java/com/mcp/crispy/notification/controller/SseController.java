package com.mcp.crispy.notification.controller;

import com.mcp.crispy.notification.service.SseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
public class SseController {

    private final SseService sseService;

    @GetMapping("/notifications/{empNo}")
    public SseEmitter subscribe(@PathVariable Long empNo) {
        return sseService.createEmitter(empNo);
    }
}
