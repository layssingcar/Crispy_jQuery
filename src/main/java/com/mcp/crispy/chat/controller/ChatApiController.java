package com.mcp.crispy.chat.controller;

import com.mcp.crispy.chat.dto.ChatMessageDto;
import com.mcp.crispy.chat.dto.ChatRoomDto;
import com.mcp.crispy.chat.dto.CrEmpDto;
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
    public List<ChatMessageDto> getMessages(@PathVariable Integer chatRoomNo) {
        return chatService.getMessages(chatRoomNo);
    }

    // 채팅방 생성
    @PostMapping("/rooms/{creatorEmpNo}/v1")
    public ResponseEntity<?> createChatRoom(@RequestBody ChatRoomDto chatRoom, @PathVariable Integer creatorEmpNo) {
        log.info("채팅방 생성: {} {} {}", chatRoom.getChatRoomNo(), chatRoom.getChatRoomTitle(), chatRoom.getCreator());
        Integer chatRoomNo = chatService.createChatRoom(chatRoom, creatorEmpNo);
        log.info("생성자 번호 : {}", creatorEmpNo);

        if(chatRoom.getParticipants() != null && !chatRoom.getParticipants().isEmpty()) {
            for(CrEmpDto participant : chatRoom.getParticipants()) {
                log.info("participant: {}", participant.toString());
                participant.setChatRoomNo(chatRoomNo);
                chatService.addParticipant(participant);
            }
        }
        log.info("채팅방 생성 및 참가자 추가 완료: {}", chatRoomNo);
        return ResponseEntity.ok().body(Map.of("message","채팅방 생성 및 초대 완료, 방 번호: " + chatRoomNo));
    }

    @PostMapping("/rooms/{chatRoomNo}/invite/v1")
    public void addParticipant(@PathVariable Integer chatRoomNo, @RequestBody CrEmpDto participant) {
        log.info("Adding participant: chatRoomNo = {}, empNo = {}", chatRoomNo, participant.getEmpNo());
        participant.setChatRoomNo(chatRoomNo);
        chatService.addParticipant(participant);
    }

    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public ChatMessageDto sendMessage(ChatMessageDto message) {
        chatService.saveMessage(message);
        log.info("메소드 호출 테스트");
        return message;
    }

    @PostMapping("/rooms/{chatRoomNo}/access/v1")
    public ResponseEntity<Void> addAccessRecord(@PathVariable Integer chatRoomNo, Authentication authentication) {
        CustomDetails userDetails = (CustomDetails) authentication.getPrincipal();
        chatService.handleAccess(chatRoomNo, userDetails.getEmpNo());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/rooms/{chatRoomNo}/entry")
    public ResponseEntity<Void> handleEntryRecord(@PathVariable Integer chatRoomNo, Authentication authentication) {
        CustomDetails userDetails = (CustomDetails) authentication.getPrincipal();
        chatService.handleEntryRecord(chatRoomNo, userDetails.getEmpNo());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/rooms/{chatRoomNo}/exit")
    public ResponseEntity<Void> handleExitRecord(@PathVariable Integer chatRoomNo, Authentication authentication) {
        CustomDetails userDetails = (CustomDetails) authentication.getPrincipal();
        chatService.handleExitRecord(chatRoomNo, userDetails.getEmpNo());
        return ResponseEntity.ok().build();
    }
}
