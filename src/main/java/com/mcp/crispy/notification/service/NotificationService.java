package com.mcp.crispy.notification.service;

import com.mcp.crispy.notification.dto.NotifyDto;
import com.mcp.crispy.notification.dto.NotifyStat;
import com.mcp.crispy.notification.mapper.NotificationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationMapper notificationMapper;
    private final SseService sseService;


    // 알림 보내기
    @Transactional
    public void sendApprovalNotification(NotifyDto notifyDto, int empNo) {
        NotifyDto notify = NotifyDto.builder()
                .notifyCt(notifyDto.getNotifyCt())
                .notifyContent(notifyDto.getNotifyContent())
                .notifyStat(NotifyStat.NOT_READ) //읽음 안 읽음
                .empNo(empNo)
                .build();

        log.info("notifyDto: {}", notify);
        notificationMapper.insertNotification(notify);
        sseService.sendNotification((long)empNo, notifyDto.getNotifyContent());
    }

    public int countUnreadNotificationsByEmpNo(int empNo) {
        return notificationMapper.countUnreadNotify(empNo);
    }

    public List<NotifyDto> getUnreadNotify(int empNo) {
        return notificationMapper.selectUnreadNotify(empNo);
    }
}
