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
            int empNo = userDetails.getEmpNo();

        return chatService.getChatRooms(empNo);
    }

    @GetMapping("/rooms/{chatRoomNo}/v1")
    public ChatRoomDto getChatRoom(@PathVariable Integer chatRoomNo, Authentication authentication) {
        CustomDetails userDetails = (CustomDetails) authentication.getPrincipal();
        return chatService.getChatRoom(chatRoomNo, userDetails.getEmpNo());
    }

    @GetMapping("/rooms/{chatRoomNo}/messages/v1")
    public List<ChatMessageDto> getMessages(@PathVariable Integer chatRoomNo) {
        return chatService.getMessages(chatRoomNo);
    }

    // 채팅방 생성
    @PostMapping("/rooms/{creatorEmpNo}/v1")
    public ResponseEntity<?> createChatRoom(@RequestBody ChatRoomDto chatRoom, @PathVariable Integer creatorEmpNo) {
        log.info("채팅방 생성: {} {} {}", chatRoom.getChatRoomNo(), chatRoom.getChatRoomTitle(), chatRoom.getCreator());
        chatService.createChatRoom(chatRoom, creatorEmpNo);
        log.info("생성자 번호 : {}", creatorEmpNo);
        return ResponseEntity.ok().build();
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
}
