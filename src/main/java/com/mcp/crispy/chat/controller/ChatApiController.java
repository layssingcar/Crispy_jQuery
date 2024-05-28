package com.mcp.crispy.chat.controller;

import com.mcp.crispy.chat.dto.ChatMessageDto;
import com.mcp.crispy.chat.dto.ChatRoomDto;
import com.mcp.crispy.chat.dto.CrEmpDto;
import com.mcp.crispy.chat.dto.UnreadMessageCountDto;
import com.mcp.crispy.chat.mapper.ChatMapper;
import com.mcp.crispy.chat.service.ChatService;
import com.mcp.crispy.common.userdetails.CustomDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatApiController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMapper chatMapper;

    @GetMapping("/rooms/v1")
    public List<ChatRoomDto> getChatRooms(Authentication authentication) {
        CustomDetails userDetails = (CustomDetails) authentication.getPrincipal();

        return chatService.getChatRooms(userDetails.getEmpNo());
    }

    @GetMapping("/rooms/{chatRoomNo}/v1")
    public ChatRoomDto getChatRoom(@PathVariable Integer chatRoomNo) {
        return chatService.getChatRoom(chatRoomNo);
    }

    @GetMapping("/rooms/{chatRoomNo}/messages/v1")
    public List<ChatMessageDto> getMessages(@PathVariable Integer chatRoomNo, Authentication authentication) {
        CustomDetails userDetails = (CustomDetails) authentication.getPrincipal();
        return chatService.getMessages(chatRoomNo, userDetails.getEmpNo());
    }

    // 채팅방 생성
    @PostMapping("/rooms/{creatorEmpNo}/v1")
    public ResponseEntity<?> createChatRoom(@RequestBody ChatRoomDto chatRoom, @PathVariable Integer creatorEmpNo) {
        Integer chatRoomNo = chatService.createAndSetupChatRoom(chatRoom, creatorEmpNo);
        return ResponseEntity.ok().body(Map.of("message", "채팅방 생성 및 초대 완료, 방 번호: " + chatRoomNo));
    }

    @PostMapping("/rooms/{chatRoomNo}/invite/v1")
    public ResponseEntity<?> inviteParticipant(@PathVariable Integer chatRoomNo, @RequestBody CrEmpDto participant) {
        chatService.addParticipantToRoom(chatRoomNo, participant);
        return ResponseEntity.ok().build(); // or return appropriate response
    }

    @PostMapping("/rooms/{chatRoomNo}/leave/v1")
    public ResponseEntity<Map<String, String>> leaveChatRoom(@PathVariable Integer chatRoomNo, Authentication authentication) {
        CustomDetails userDetails = (CustomDetails) authentication.getPrincipal();
        chatService.updateEntryStat(chatRoomNo, userDetails.getEmpNo());
        return ResponseEntity.ok().body(Map.of("message","채팅방을 나갔습니다."));
    }

    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public ChatMessageDto sendMessage(ChatMessageDto message) {
        chatService.sendMessage(message);
        log.info("메소드 호출 테스트");

        int totalUnread = chatService.getUnreadCounts(message.getEmpNo());
        messagingTemplate.convertAndSendToUser(message.getEmpNo().toString(), "/queue/unreadCount", totalUnread);
        messagingTemplate.convertAndSend("/topic/roomUpdate", chatService.getChatRooms(message.getEmpNo()));
        return message;
    }

    @MessageMapping("/fetchUnreadCount")
    public void fetchUnreadCounts(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
                // 관리자는 읽지 않은 메시지 개수를 계산하지 않습니다.
                return;
            }
            CustomDetails userDetails = (CustomDetails) authentication.getPrincipal();
            int totalUnread = chatService.getUnreadCounts(userDetails.getEmpNo());
            log.info("total Unread: {}", totalUnread);
            // 사용자에게 개수 반환
            messagingTemplate.convertAndSendToUser(userDetails.getUsername(), "/queue/unreadCount", totalUnread);
        }
    }

    @PostMapping("/rooms/{chatRoomNo}/access/v1")
    public ResponseEntity<Void> addAccessRecord(@PathVariable Integer chatRoomNo, Authentication authentication) {
        CustomDetails userDetails = (CustomDetails) authentication.getPrincipal();
        chatService.handleAccess(chatRoomNo, userDetails.getEmpNo());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/rooms/{chatRoomNo}/entry/{empNo}/v2")
    public ResponseEntity<Void> handleEntryRecord(@PathVariable Integer chatRoomNo, @PathVariable Integer empNo) {
        chatService.handleEntryRecord(chatRoomNo, empNo);
        log.info("ChatRoom No : {} {}", chatRoomNo, empNo);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/rooms/{chatRoomNo}/exit/v1")
    public ResponseEntity<Void> handleExitRecord(@PathVariable Integer chatRoomNo, Authentication authentication) {
        CustomDetails userDetails = (CustomDetails) authentication.getPrincipal();
        chatService.handleExitRecord(chatRoomNo, userDetails.getEmpNo());
        return ResponseEntity.ok().build();
    }

    // 읽지 않은 메시지 개수
    @GetMapping("/rooms/unread-count/v1")
    public ResponseEntity<List<UnreadMessageCountDto>> getUnreadMessageCount(Authentication authentication) {
        CustomDetails userDetails = (CustomDetails) authentication.getPrincipal();
        List<UnreadMessageCountDto> unreadMessageCount = chatService.getUnreadMessageCount(userDetails.getEmpNo());
        log.info("unreadMessageCount: {}", unreadMessageCount);
        return ResponseEntity.ok(unreadMessageCount);
    }

    // 알림 상태 토글
    @PostMapping("/rooms/{chatRoomNo}/toggleAlarm/v1")
    public ResponseEntity<Void> toggleAlarm(@PathVariable Integer chatRoomNo, Authentication authentication) {
        CustomDetails userDetails = (CustomDetails) authentication.getPrincipal();
        chatService.toggleAlarmStat(chatRoomNo, userDetails.getEmpNo());
        return ResponseEntity.ok().build();
    }
}
