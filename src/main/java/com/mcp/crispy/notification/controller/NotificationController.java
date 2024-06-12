package com.mcp.crispy.notification.controller;

import com.mcp.crispy.notification.dto.NotifyDto;
import com.mcp.crispy.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/unreadCount/{empNo}")
    public ResponseEntity<Map<String, Integer>> getUnreadCount(@PathVariable("empNo") int empNo) {
        int count = notificationService.countUnreadNotificationsByEmpNo(empNo);
        return ResponseEntity.ok(Map.of("count", count));
    }

    @GetMapping("/unread/{empNo}")
    public ResponseEntity<List<NotifyDto>> getUnread(@PathVariable("empNo") int empNo) {
        List<NotifyDto> notifications = notificationService.getUnreadNotify(empNo);
        return ResponseEntity.ok(notifications);
    }
}

