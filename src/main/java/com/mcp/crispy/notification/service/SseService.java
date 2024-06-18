package com.mcp.crispy.notification.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mcp.crispy.notification.dto.NotifyDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class SseService {

    public static final int ADMIN_NO = 0;


    private final Map<Long, SseEmitter> emitters = new HashMap<>();
    public SseEmitter createEmitter(Long empNo) {
        if (empNo == ADMIN_NO) {
            log.info("관리자용 emitter 생성");
            return createAdminEmitter();
        }
        log.info("호출됐나요?");
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.put(empNo, emitter);
        emitter.onCompletion(() -> emitters.remove(empNo));
        emitter.onTimeout(() -> emitters.remove(empNo));
        emitter.onError((e) -> emitters.remove(empNo));

        // Heartbeat 전송
        new Thread(() -> {
            try {
                while (true) {
                    Thread.sleep(15000); // 15초마다 heartbeat 전송
                    emitter.send(SseEmitter.event().name("heartbeat").data("heartbeat"));
                }
            } catch (Exception e) {
                emitters.remove(empNo);
            }
        }).start();

        return emitter;
    }

    private SseEmitter createAdminEmitter() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.put((long) ADMIN_NO, emitter);
        emitter.onCompletion(() -> log.info("관리자 Emitter 완료"));
        emitter.onTimeout(() -> log.info("관리자 Emitter 시간 초과"));
        emitter.onError((e) -> log.info("관리자 Emitter 에러 발생: {}", e.getMessage()));

        // Heartbeat 전송
        new Thread(() -> {
            try {
                while (true) {
                    Thread.sleep(15000); // 15초마다 heartbeat 전송
                    emitter.send(SseEmitter.event().name("heartbeat").data("heartbeat"));
                }
            } catch (Exception e) {
                log.info("관리자 Emitter 에러 발생");
            }
        }).start();

        return emitter;
    }

    public void sendNotification(Long empNo, NotifyDto notifyDto) {
        SseEmitter emitter = emitters.get(empNo);
        log.info("sendNotification: {}", emitter);
        if (emitter != null) {
            try {
                // JSON 형식으로 변환하여 전송
                String jsonData = new ObjectMapper().writeValueAsString(notifyDto);
                emitter.send(SseEmitter.event().name("notification").data(jsonData));
                log.info("전송됐나요?");
            } catch (Exception e) {
                emitters.remove(empNo);
            }
        }
    }

}
