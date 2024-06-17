package com.mcp.crispy.notification.controller;

import com.mcp.crispy.notification.dto.NotifyDto;
import com.mcp.crispy.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

    @PutMapping("/read/{notifyNo}")
    public ResponseEntity<Void> markAsRead(@PathVariable int notifyNo) {
        notificationService.markAsRead(notifyNo);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/crispy/approval-list/sign")
    public String showApprovalListSign(@RequestParam(value = "notifyNo", required = false) Integer notifyNo, Model model) {
        if (notifyNo != null) {
            notificationService.markAsRead(notifyNo);
        }
        // 나머지 로직 추가
        return "approval-list/sign";
    }
}

