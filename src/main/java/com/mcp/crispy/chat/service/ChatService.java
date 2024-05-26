package com.mcp.crispy.chat.service;

import com.mcp.crispy.chat.dto.*;
import com.mcp.crispy.chat.mapper.ChatMapper;
import com.mcp.crispy.employee.dto.EmployeeDto;
import com.mcp.crispy.employee.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMapper chatMapper;
    private final EmployeeService employeeService;

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

    // 채팅방 번호와 사용자 번호를 기반으로 메시지 목록을 조회
    @Transactional
    public List<ChatMessageDto> getMessages(Integer chatRoomNo, Integer empNo) {
        return chatMapper.getMessages(chatRoomNo, empNo);
    }

    // 메시지를 저장하고 관련된 모든 참가자의 상태를 업데이트
    @Transactional
    public ChatMessageDto sendMessage(ChatMessageDto message) {

        // 메시지 저장 및 사용자 정보 풍부화
        message = saveMessage(message);

        // 채팅방의 모든 참가자 조회
        List<CrEmpDto> participants = chatMapper.getParticipantsByRoom(message.getChatRoomNo());

        // 1:1 채팅방 확인 및 상태 업데이트
        if (participants.size() == 2) {
            // 모든 참가자의 상태 업데이트 (보내는 사람 제외)
            ChatMessageDto finalMessage = message;
            participants.stream()
                    .filter(participant -> !participant.getEmpNo().equals(finalMessage.getEmpNo()))
                    .forEach(participant -> {
                        if (participant.getEntryStat() == EntryStat.INACTIVE.getStatus()) { // 참가자가 '나간 상태'일 경우
                            participant.setEntryStat(EntryStat.ACTIVE.getStatus()); // 상태를 '활성화'로 변경
                            chatMapper.updateParticipantEntryStat(participant); // DB 업데이트
                            chatMapper.updateEntryRecord(finalMessage.getChatRoomNo(), participant.getEmpNo());
                            log.info("finalMessage: {}", participant.getEmpNo());
                        }
                    });
        }

        log.info("Message sent and participants updated: {}", message);
        return message;
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

    // 새 채팅방을 생성
    public Integer createChatRoom(ChatRoomDto chatRoom, Integer creatorEmpNo) {
        chatMapper.createChatRoom(chatRoom);
        CrEmpDto crEmpDto = CrEmpDto.builder()
                .chatRoomNo(chatRoom.getChatRoomNo())
                .empNo(creatorEmpNo)
                .entryStat(EntryStat.ACTIVE.getStatus())
                .alarmStat(AlarmStat.ACTIVE.getStatus())
                .build();
        chatMapper.addParticipant(crEmpDto);
        return chatRoom.getChatRoomNo();
    }

    // 채팅방에 초대 시 참가자에게 채팅방 번호 부여
    public void addParticipantToRoom(Integer chatRoomNo, CrEmpDto participant) {
        participant.setChatRoomNo(chatRoomNo);
        addParticipant(participant);
    }

    // 새 채팅방을 생성하고 해당 채팅방에 참가자들을 추가합니다.
    public Integer createAndSetupChatRoom(ChatRoomDto chatRoom, Integer creatorEmpNo) {
        Integer chatRoomNo = createChatRoom(chatRoom, creatorEmpNo);
        for (CrEmpDto participant : chatRoom.getParticipants()) {
            participant.setChatRoomNo(chatRoomNo);
            addParticipant(participant);
        }
        return chatRoomNo;
    }

    // 채팅방에 참가자 추가
    public void addParticipant(CrEmpDto participant) {
        CrEmpDto existingParticipant = chatMapper.getParticipant(participant.getChatRoomNo(), participant.getEmpNo());
        if (existingParticipant != null) {
            // 참가자가 이미 존재하면
            participant.setEntryStat(EntryStat.ACTIVE.getStatus());
            participant.setAlarmStat(AlarmStat.ACTIVE.getStatus());
            chatMapper.updateParticipantEntryStat(participant);
        } else {
            // 참가자가 존재하지 않으면 추가
            participant.setEntryStat(EntryStat.ACTIVE.getStatus());
            participant.setAlarmStat(AlarmStat.ACTIVE.getStatus());
            chatMapper.addParticipant(participant);
        }
    }

    // 참가자 입장 상태 관리
    public void updateEntryStat(Integer chatRoomNo, Integer empNo) {
        CrEmpDto participant = CrEmpDto.builder()
                .chatRoomNo(chatRoomNo)
                .empNo(empNo)
                .entryStat(EntryStat.INACTIVE.getStatus())
                .alarmStat(AlarmStat.INACTIVE.getStatus())
                .build();
        chatMapper.updateEntryStat(participant);
    }

    // 접속 기록 관리 최초 삽입 이후 시간 업데이트
    @Async
    @Transactional
    public void handleAccess(Integer chatRoomNo, Integer empNo) {
        Integer count = chatMapper.checkAccessExists(chatRoomNo, empNo);
        if(count != null && count > 0) {
            chatMapper.updateAccessRecord(chatRoomNo, empNo);
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
}
