package com.mcp.crispy.chat.service;

import com.mcp.crispy.auth.domain.EmployeePrincipal;
import com.mcp.crispy.chat.dto.*;
import com.mcp.crispy.chat.mapper.ChatMapper;
import com.mcp.crispy.employee.dto.EmployeeDto;
import com.mcp.crispy.employee.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMapper chatMapper;
    private final EmployeeService employeeService;
    private final SimpMessagingTemplate messagingTemplate;

    // 사용자 번호를 기반으로 채팅방 목록을 조회
    public List<ChatRoomDto> getChatRooms(Integer empNo) {
        List<ChatRoomDto> chatRooms = chatMapper.getChatRooms(empNo);
        for (ChatRoomDto room : chatRooms) {
            room.setCreator(chatRooms.get(0).getCreator());
            List<CrEmpDto> participants = Optional.ofNullable(room.getParticipants())
                    .orElseGet(Collections::emptyList)
                    .stream()
                    .filter(p -> !p.getEmpNo().equals(empNo))
                    .collect(Collectors.toList());
            room.setParticipants(participants);
            log.info("participants: {}", participants);
        }
        log.info(chatRooms.toString());
        return chatRooms;
    }

    // 채팅방 번호를 기반으로 채팅방 정보를 조회
    public ChatRoomDto getChatRoom(Integer chatRoomNo) {
        ChatRoomDto chatRoom = chatMapper.getChatRoom(chatRoomNo);
        List<CrEmpDto> participants = chatMapper.getParticipantsByRoom(chatRoomNo);
        chatRoom.setParticipants(participants);
        log.info("chatRoom: {}", chatRoom.toString());
        return chatRoom;
    }

    // 채팅방 인원 호출
    public List<CrEmpDto> getParticipants(Integer chatRoomNo) {
        return chatMapper.getParticipantsByRoom(chatRoomNo);
    }

    // 채팅방 번호와 사용자 번호를 기반으로 메시지 목록을 조회
    @Transactional
    public List<ChatMessageDto> getMessages(Integer chatRoomNo, Integer empNo) {
        return chatMapper.getMessages(chatRoomNo, empNo);
    }

    // 메시지를 저장하고 관련된 모든 참가자의 상태를 업데이트
    @Transactional
    public ChatMessageDto sendMessageAndUpdate(ChatMessageDto message) {

        // 채팅방의 모든 참가자 조회
        List<CrEmpDto> participants = chatMapper.getParticipantsByRoom(message.getChatRoomNo());

        // 1:1 채팅방 확인 및 상태 업데이트
        if (participants.size() == 2) {
            ChatMessageDto finalMessage = message;
            participants.stream()
                    .filter(participant -> !participant.getEmpNo().equals(finalMessage.getEmpNo()))
                    .forEach(participant -> {
                        if (participant.getEntryStat() == EntryStat.of(EntryStat.INACTIVE.getStatus())) { // 참가자가 '나간 상태'일 경우
                            participant.setEntryStat(EntryStat.of(EntryStat.ACTIVE.getStatus())); // 상태를 '활성화'로 변경
                            participant.setAlarmStat(AlarmStat.of(AlarmStat.ACTIVE.getStatus())); // 상태를 '활성화'로 변경
                            chatMapper.updateParticipantEntryStat(participant); // DB 업데이트
                            chatMapper.updateEntryRecord(finalMessage.getChatRoomNo(), participant.getEmpNo());
                            log.info("finalMessage: {}", participant.getEmpNo());
                        }
                    });
        }

        // 메시지 저장
        message = saveMessage(message);

        // 메시지를 보낸 사용자와 수신자의 상태 업데이트
        updateSenderAndReceiverStatus(message);

        for (CrEmpDto participant : participants) {
            List<ChatRoomDto> chatRooms = getChatRooms(participant.getEmpNo());
            messagingTemplate.convertAndSendToUser(participant.getEmpId(), "/queue/roomUpdate", chatRooms);
        }

        log.info("Message sent and participants updated: {}", message);
        return message;
    }

    @Transactional
    public void updateSenderAndReceiverStatus(ChatMessageDto message) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            EmployeePrincipal senderDetails = (EmployeePrincipal) auth.getPrincipal();

            // 송신자에게 읽지 않은 메시지 개수 업데이트
            int totalUnread = getUnreadCounts(message.getEmpNo());
            messagingTemplate.convertAndSendToUser(senderDetails.getUsername(), "/queue/unreadCount", totalUnread);

            // 송신자에게 채팅방 목록 업데이트
            List<ChatRoomDto> chatRooms = getChatRooms(message.getEmpNo());
            messagingTemplate.convertAndSendToUser(senderDetails.getUsername(), "/queue/roomUpdate", chatRooms);

            // 해당 채팅방의 참가자 목록 가져오기
            List<CrEmpDto> participants = getParticipants(message.getChatRoomNo());
            for (CrEmpDto participant : participants) {
                if (!participant.getEmpNo().equals(message.getEmpNo())) {
                    // 수신자에게 새로운 메시지 전송
                    messagingTemplate.convertAndSendToUser(participant.getEmpId(), "/queue/messages", message);

                    // 수신자에게 채팅방 목록 업데이트
                    messagingTemplate.convertAndSendToUser(participant.getEmpId(), "/queue/roomUpdate", chatRooms);

                    // 수신자에게 읽지 않은 메시지 개수 업데이트
                    int receiverUnread = getUnreadCounts(participant.getEmpNo());
                    messagingTemplate.convertAndSendToUser(participant.getEmpId(), "/queue/unreadCount", receiverUnread);
                }
            }
        } else {
            log.error("인증상태가 잘못되었습니다.");
        }
    }


    @Transactional
    public ChatMessageDto saveMessage(ChatMessageDto message) {
        chatMapper.saveMessage(message);
        EmployeeDto employee = employeeService.getEmployeeDetailsByEmpNo(message.getEmpNo());
        message.setMsgDt(Date.from(Instant.now()));
        message.setEmpName(employee.getEmpName());
        message.setEmpProfile(employee.getEmpProfile());
        log.info("msgDt: {}", String.valueOf(message.getMsgDt()));
        return message;
    }

    @Transactional
    // 새 채팅방을 생성
    public Integer createChatRoom(ChatRoomDto chatRoom, Integer creatorEmpNo) {
        chatMapper.createChatRoom(chatRoom);
        CrEmpDto crEmpDto = CrEmpDto.builder()
                .chatRoomNo(chatRoom.getChatRoomNo())
                .empNo(creatorEmpNo)
                .entryStat(EntryStat.of(EntryStat.ACTIVE.getStatus()))
                .alarmStat(AlarmStat.of(AlarmStat.ACTIVE.getStatus()))
                .build();
        chatMapper.addParticipant(crEmpDto);
        return chatRoom.getChatRoomNo();
    }

    @Transactional
    // 채팅방에 초대 시 참가자에게 채팅방 번호 부여
    public void addParticipantToRoom(Integer chatRoomNo, CrEmpDto participant) {
        participant.setChatRoomNo(chatRoomNo);
        addParticipant(participant);
    }

    @Transactional
    // 새 채팅방을 생성하고 해당 채팅방에 참가자들을 추가합니다.
    public Integer createAndSetupChatRoom(ChatRoomDto chatRoom, Integer creatorEmpNo) {
        Integer chatRoomNo = createChatRoom(chatRoom, creatorEmpNo);
        for (CrEmpDto participant : chatRoom.getParticipants()) {
            participant.setChatRoomNo(chatRoomNo);
            addParticipant(participant);
            addEntryRecord(chatRoomNo, participant.getEmpNo());
        }

        addEntryRecord(chatRoomNo, creatorEmpNo);
        return chatRoomNo;
    }

    // 채팅방에 참가자 추가
    public void addParticipant(CrEmpDto participant) {
        CrEmpDto existingParticipant = chatMapper.getParticipant(participant.getChatRoomNo(), participant.getEmpNo());
        participant.setEntryStat(EntryStat.of(EntryStat.ACTIVE.getStatus()));
        participant.setAlarmStat(AlarmStat.of(AlarmStat.ACTIVE.getStatus()));
        if (existingParticipant != null) {
            // 참가자가 이미 존재하면
            chatMapper.updateParticipantEntryStat(participant);
        } else {
            // 참가자가 존재하지 않으면 추가
            chatMapper.addParticipant(participant);
        }
    }

    // 참가자 입장 상태 관리
    public void updateEntryStat(Integer chatRoomNo, Integer empNo) {
        log.info("EntryStat: {}", chatRoomNo);
        CrEmpDto participant = CrEmpDto.builder()
                .chatRoomNo(chatRoomNo)
                .empNo(empNo)
                .entryStat(EntryStat.of(EntryStat.INACTIVE.getStatus()))
                .alarmStat(AlarmStat.of(AlarmStat.INACTIVE.getStatus()))
                .build();
        chatMapper.updateEntryStat(participant);
    }

    // 접속 기록 관리 최초 삽입 이후 시간 업데이트
    @Transactional
    public void handleAccess(Integer chatRoomNo, Integer empNo) {
        Integer count = chatMapper.checkAccessExists(chatRoomNo, empNo);
        if(count != null && count > 0) {
            chatMapper.updateAccessRecord(chatRoomNo, empNo);
            log.info("access 업데이트");
        } else {
            chatMapper.addAccessRecord(chatRoomNo, empNo);
        }
    }

    // 입장 기록 관리
    @Transactional
    public void handleEntryRecord(Integer chatRoomNo, Integer empNo) {
        Integer count = chatMapper.checkEntryRecordExists(chatRoomNo, empNo);
        if(count != null && count > 0) {
            chatMapper.updateEntryRecord(chatRoomNo, empNo);
        } else {
            chatMapper.insertEntryRecord(chatRoomNo, empNo);
        }
    }

    // 퇴장 기록 관리
    @Transactional
    public void handleExitRecord(Integer chatRoomNo, Integer empNo) {
        chatMapper.updateExitRecord(chatRoomNo, empNo);
    }

    @Transactional
    public void addEntryRecord(Integer chatRoomNo, Integer empNo) {
        chatMapper.insertEntryRecord(chatRoomNo, empNo);
    }


    // 각 방마다의 읽지 않은 메시지 개수
    @Transactional
    public List<UnreadMessageCountDto> getUnreadMessageCount(Integer empNo) {
        return chatMapper.countUnreadMessages(empNo);
    }

    // 읽지 않은 메시지 총합
    @Transactional
    public int getUnreadCounts(Integer empNo) {
        List<UnreadMessageCountDto> unreadCounts = chatMapper.countUnreadMessages(empNo);
        log.info("unreadCounts: {}", unreadCounts);
        return unreadCounts.stream()
                .filter(Objects::nonNull)
                .mapToInt(UnreadMessageCountDto::getUnreadCount)
                .sum();
    }

    // 알림 상태 토글
    @Transactional
    public void toggleAlarmStat(Integer chatRoomNo, Integer empNo) {
        chatMapper.toggleAlarmStat(chatRoomNo, empNo);
    }

    public Date getLastAccessTime(Integer chatRoomNo, Integer empNo) {
        return chatMapper.getLastAccessTime(chatRoomNo, empNo);
    }

    public List<ChatMessageDto> getUnreadMessages(Integer chatRoomNo, Integer empNo) {
        return chatMapper.getUnreadMessages(chatRoomNo, empNo);
    }
}
