package com.mcp.crispy.chat.service;

import com.mcp.crispy.chat.dto.ChatMessageDto;
import com.mcp.crispy.chat.dto.ChatRoomDto;
import com.mcp.crispy.chat.dto.CrEmpDto;
import com.mcp.crispy.chat.mapper.ChatMapper;
import com.mcp.crispy.employee.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMapper chatMapper;
    private final EmployeeService employeeService;

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

    public ChatRoomDto getChatRoom(Integer chatRoomNo) {
        ChatRoomDto chatRoom = chatMapper.getChatRoom(chatRoomNo);
        List<CrEmpDto> participants = chatMapper.getParticipantsByRoom(chatRoomNo);
        chatRoom.setParticipants(participants);
        log.info("chatRoom: {}", chatRoom.toString());
        return chatRoom;
    }

    public List<ChatMessageDto> getMessages(Integer chatRoomNo) {
        return chatMapper.getMessages(chatRoomNo);
    }

    public void saveMessage(ChatMessageDto message) {
        chatMapper.saveMessage(message);
    }

    public Integer createChatRoom(ChatRoomDto chatRoom, Integer creatorEmpNo) {
        chatMapper.createChatRoom(chatRoom);
         CrEmpDto crEmpDto = CrEmpDto.builder()
                .chatRoomNo(chatRoom.getChatRoomNo())
                .empNo(creatorEmpNo)
                .entryStat(0)
                .build();
         chatMapper.addParticipant(crEmpDto);
         return chatRoom.getChatRoomNo();
    }

    public void addParticipant(CrEmpDto participant) {
        participant.setEntryStat(0);
        chatMapper.addParticipant(participant);
    }

    // 접속 기록 (최초 삽입 이후 수정)
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

    @Transactional
    public void handleEntryRecord(Integer chatRoomNo, Integer empNo) {
        Integer count = chatMapper.checkEntryRecordExists(chatRoomNo, empNo);
        if(count != null && count > 0) {
            chatMapper.updateEntryRecord(chatRoomNo, empNo);
        } else {
            chatMapper.insertEntryRecord(chatRoomNo, empNo);
        }
    }

    @Transactional
    public void handleExitRecord(Integer chatRoomNo, Integer empNo) {
        chatMapper.updateExitRecord(chatRoomNo, empNo);
    }
}
