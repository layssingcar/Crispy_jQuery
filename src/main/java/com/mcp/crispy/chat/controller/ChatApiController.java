package com.mcp.crispy.chat.controller;

import com.mcp.crispy.auth.domain.EmployeePrincipal;
import com.mcp.crispy.chat.dto.ChatMessageDto;
import com.mcp.crispy.chat.dto.ChatRoomDto;
import com.mcp.crispy.chat.dto.CrEmpDto;
import com.mcp.crispy.chat.dto.UnreadMessageCountDto;
import com.mcp.crispy.chat.mapper.ChatMapper;
import com.mcp.crispy.chat.service.ChatService;
import com.mcp.crispy.employee.dto.EmployeeDto;
import com.mcp.crispy.employee.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatApiController {

    private final ChatService chatService;
    private final EmployeeService employeeService;
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMapper chatMapper;


    /**
     * 채팅방 목록  가져오기
     * 배영욱 (24. 05. 24)
     * @param authentication
     * @return
     */
    @GetMapping("/rooms/v1")
    public List<ChatRoomDto> getChatRooms(Authentication authentication) {
        EmployeePrincipal userDetails = (EmployeePrincipal) authentication.getPrincipal();

        return chatService.getChatRooms(userDetails.getEmpNo());
    }

    /**
     * 특정 채팅방 조회
     * 배영욱 (24. 05. 24)
     * @param chatRoomNo
     * @return
     */
    @GetMapping("/rooms/{chatRoomNo}/v1")
    public ChatRoomDto getChatRoom(@PathVariable Integer chatRoomNo) {
        return chatService.getChatRoom(chatRoomNo);
    }

    /**
     * 메시지 가져오기
     * 배영욱 (24. 05. 26)
     * @param chatRoomNo
     * @param authentication
     * @return
     */
    @GetMapping("/rooms/{chatRoomNo}/messages/v1")
    public List<ChatMessageDto> getMessages(@PathVariable Integer chatRoomNo, Authentication authentication) {
        EmployeePrincipal userDetails = (EmployeePrincipal) authentication.getPrincipal();
        return chatService.getMessages(chatRoomNo, userDetails.getEmpNo());
    }

    /**
     * 채팅방 생성
     * 배영욱 (24. 05. 26)
     * @param chatRoom
     * @param creatorEmpNo
     * @return
     */
    @PostMapping("/rooms/{creatorEmpNo}/v1")
    public ResponseEntity<?> createChatRoom(@RequestBody ChatRoomDto chatRoom, @PathVariable Integer creatorEmpNo) {
        Integer chatRoomNo = chatService.createAndSetupChatRoom(chatRoom, creatorEmpNo);
        return ResponseEntity.ok().body(Map.of("message", "채팅방 생성 및 초대 완료, 방 번호: " + chatRoomNo));
    }

    /**
     * 유저 초대
     * 배영욱 (24. 05. 28)
     * @param chatRoomNo
     * @param participant
     * @return
     */
    @PostMapping("/rooms/{chatRoomNo}/invite/v1")
    public ResponseEntity<?> inviteParticipant(@PathVariable Integer chatRoomNo, @RequestBody CrEmpDto participant) {
        chatService.addParticipantToRoom(chatRoomNo, participant);
        List<CrEmpDto> participants = chatService.getParticipants(chatRoomNo); // 전체 참가자 목록 가져오기
        messagingTemplate.convertAndSend("/topic/roomParticipants/" + chatRoomNo, participants);
        return ResponseEntity.ok().body(Map.of("message", "초대가 완료되었습니다."));
    }

    /**
     * 채팅방 나가기
     * 배영욱 (24. 05. 26)
     * @param chatRoomNo
     * @param authentication
     * @return
     */
    @PostMapping("/rooms/{chatRoomNo}/leave/v1")
    public ResponseEntity<Map<String, String>> leaveChatRoom(@PathVariable Integer chatRoomNo, Authentication authentication) {
        EmployeePrincipal userDetails = (EmployeePrincipal) authentication.getPrincipal();
        chatService.updateEntryStat(chatRoomNo, userDetails.getEmpNo());
        return ResponseEntity.ok().body(Map.of("message","채팅방을 나갔습니다."));
    }

    /**
     * 메시지 보내기
     * 배영욱 (24. 05. 28)
     * @param message
     * @return
     */
    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public ChatMessageDto sendMessage(ChatMessageDto message) {
        chatService.sendMessageAndUpdate(message);
        return message;
    }

    /**
     * 방 업데이트
     * 배영욱 (24. 05. 28)
     * @param authentication
     */
    @MessageMapping("/roomUpdate")
    public void roomUpdate(Authentication authentication) {
        if(authentication != null && authentication.isAuthenticated()) {
            EmployeePrincipal userDetails = (EmployeePrincipal) authentication.getPrincipal();
            List<ChatRoomDto> chatRooms = chatService.getChatRooms(userDetails.getEmpNo());
            messagingTemplate.convertAndSendToUser(userDetails.getUsername(), "/queue/roomUpdate", chatRooms);
        }
    }

    /**
     * 안 읽은 메시지 개수 계산
     * 배영욱 (24. 05. 28)
     * @param authentication
     */
    @MessageMapping("/fetchUnreadCount")
    @SendToUser("/queue/unreadCount")
    public void fetchUnreadCounts(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
                // 관리자는 읽지 않은 메시지 개수를 계산하지 않습니다.
                return;
            }
            EmployeePrincipal userDetails = (EmployeePrincipal) authentication.getPrincipal();
            int totalUnread = chatService.getUnreadCounts(userDetails.getEmpNo());
            log.info("total Unread: {}", totalUnread);
            // 사용자에게 개수 반환
            messagingTemplate.convertAndSendToUser(userDetails.getUsername(), "/queue/unreadCount", totalUnread);
        }
    }

    /**
     * 채팅방 접속시간 저장
     * 배영욱 (24. 05. 28)
     * @param chatRoomNo
     * @param authentication
     * @return
     */
    @PostMapping("/rooms/{chatRoomNo}/access/v1")
    public ResponseEntity<?> addAccessRecord(@PathVariable Integer chatRoomNo, Authentication authentication) {
        EmployeePrincipal userDetails = (EmployeePrincipal) authentication.getPrincipal();
        chatService.handleAccess(chatRoomNo, userDetails.getEmpNo());
        int totalUnread = chatService.getUnreadCounts(userDetails.getEmpNo());
        log.info("총 갯수 몇개: {}", totalUnread);
        messagingTemplate.convertAndSendToUser(userDetails.getUsername(), "/queue/unreadCount", totalUnread);
        return ResponseEntity.ok(Map.of("unreadCount", totalUnread));
    }

    /**
     * 입장 시간 관리
     * 배영욱 (24. 05. 26)
     * @param chatRoomNo
     * @param empNo
     * @return
     */
    @PostMapping("/rooms/{chatRoomNo}/entry/{empNo}/v2")
    public ResponseEntity<Void> handleEntryRecord(@PathVariable Integer chatRoomNo, @PathVariable Integer empNo) {
        chatService.handleEntryRecord(chatRoomNo, empNo);
        log.info("ChatRoom No : {} {}", chatRoomNo, empNo);
        return ResponseEntity.ok().build();
    }

    /**
     * 퇴장 시간 관리
     * 배영욱 (24. 05. 24)
     * @param chatRoomNo
     * @param authentication
     * @return
     */
    @PostMapping("/rooms/{chatRoomNo}/exit/v1")
    public ResponseEntity<Void> handleExitRecord(@PathVariable Integer chatRoomNo, Authentication authentication) {
        EmployeePrincipal userDetails = (EmployeePrincipal) authentication.getPrincipal();
        chatService.handleExitRecord(chatRoomNo, userDetails.getEmpNo());
        return ResponseEntity.ok().build();
    }


    /**
     * 읽지 않은 메시지 개수 계산
     * 배영욱 (24. 05. 27)
     * @param authentication
     * @return
     */
    @GetMapping("/rooms/unreadCount/v1")
    public ResponseEntity<List<UnreadMessageCountDto>> getUnreadMessageCount(Authentication authentication) {
        EmployeePrincipal userDetails = (EmployeePrincipal) authentication.getPrincipal();
        List<UnreadMessageCountDto> unreadMessageCount = chatService.getUnreadMessageCount(userDetails.getEmpNo());
        log.info("unreadMessageCount: {}", unreadMessageCount);
        return ResponseEntity.ok(unreadMessageCount);
    }

    /**
     * 알림 상태 토글
     * 배영욱 (24. 05. 28)
     * @param chatRoomNo
     * @param authentication
     * @return
     */
    @PostMapping("/rooms/{chatRoomNo}/toggleAlarm/v1")
    public ResponseEntity<Void> toggleAlarm(@PathVariable Integer chatRoomNo, Authentication authentication) {
        EmployeePrincipal userDetails = (EmployeePrincipal) authentication.getPrincipal();
        chatService.toggleAlarmStat(chatRoomNo, userDetails.getEmpNo());
        return ResponseEntity.ok().build();
    }

    /**
     * 나를 제외한 전 직원 목록 호출
     * 배영욱 (24. 05. 28)
     * @param authentication
     * @return
     */
    @GetMapping("/employees/v1")
    public ResponseEntity<List<EmployeeDto>> getAllEmployees(Authentication authentication) {
        EmployeePrincipal userDetails = (EmployeePrincipal) authentication.getPrincipal();;
        List<EmployeeDto> employees = employeeService.getAllEmployees(userDetails.getEmpNo());
        return ResponseEntity.ok(employees);
    }

    /**
     * 채팅 초대 할때 검색하는 메소드
     * 배영욱 (24. 05. 28)
     * @param employeeDto
     * @param authentication
     * @return employees
     */
    @PostMapping("/employees/search/v1")
    public ResponseEntity<List<EmployeeDto>> searchEmployees(@RequestBody EmployeeDto employeeDto, Authentication authentication) {
        EmployeePrincipal userDetails = (EmployeePrincipal) authentication.getPrincipal();
        List<EmployeeDto> employees = employeeService.getSearchEmployees(employeeDto, userDetails.getEmpNo());
        log.info("employees: {}", employees);
        return ResponseEntity.ok(employees);
    }

    /**
     * 채팅방 내에 존재하는 직원을 제외한 초대목록
     * 배영욱 (24. 05. 28)
     * @param chatRoomNo
     * @return
     */
    @GetMapping("/inviteEmployee/{chatRoomNo}/v1")
    public ResponseEntity<List<EmployeeDto>> getInviteEmployees(@PathVariable Integer chatRoomNo) {
        List<EmployeeDto> inviteEmployees = employeeService.getInviteEmployees(chatRoomNo);
        return ResponseEntity.ok(inviteEmployees);
    }


    /**
     * 채팅방 목록 전송
     * 배영욱 (24. 05. 28)
     * @param authentication
     */
    @MessageMapping("/fetchChatRooms")
    @SendToUser("/queue/chatRooms")
    public void fetchChatRooms(Authentication authentication) {
        EmployeePrincipal userDetails = (EmployeePrincipal) authentication.getPrincipal();
        List<ChatRoomDto> chatRooms = chatService.getChatRooms(userDetails.getEmpNo());
        log.info("Fetching chat rooms for user: {}", userDetails.getUsername()); // 추가된 로그
        log.info("Fetching chat rooms for user: {}", chatRooms.toString());
        // 사용자에게 채팅방 목록 전송
        messagingTemplate.convertAndSendToUser(userDetails.getUsername(), "/queue/chatRooms", chatRooms);
    }

    /**
     * 마지막 접속 시간 계산
     * 배영욱 (24. 05. 28)
     * @param chatRoomNo
     * @param authentication
     * @return
     */
    @GetMapping("/rooms/{chatRoomNo}/lastAccessTime/v1")
    public ResponseEntity<Date> getLastAccessTime(@PathVariable Integer chatRoomNo, Authentication authentication) {
        EmployeePrincipal userDetails = (EmployeePrincipal) authentication.getPrincipal();
        Date lastAccessTime = chatService.getLastAccessTime(chatRoomNo, userDetails.getEmpNo());
        return ResponseEntity.ok(lastAccessTime);
    }

    @GetMapping("/rooms/{chatRoomNo}/unreadMessages/v1")
    public ResponseEntity<List<ChatMessageDto>> getUnreadMessages(@PathVariable Integer chatRoomNo, Authentication authentication) {
        EmployeePrincipal userDetails = (EmployeePrincipal) authentication.getPrincipal();
        List<ChatMessageDto> unreadMessages = chatService.getUnreadMessages(chatRoomNo, userDetails.getEmpNo());
        return ResponseEntity.ok(unreadMessages);
    }

}
