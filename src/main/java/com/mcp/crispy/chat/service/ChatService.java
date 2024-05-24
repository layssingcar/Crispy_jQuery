package com.mcp.crispy.chat.service;

import com.mcp.crispy.chat.dto.ChatMessageDto;
import com.mcp.crispy.chat.dto.ChatRoomDto;
import com.mcp.crispy.chat.dto.CrEmpDto;
import com.mcp.crispy.chat.mapper.ChatMapper;
import com.mcp.crispy.employee.dto.EmployeeDto;
import com.mcp.crispy.employee.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMapper chatMapper;
    private final EmployeeService employeeService;

    public List<ChatRoomDto> getChatRooms(Integer empNo) {
        return chatMapper.getChatRooms(empNo);
    }

    public ChatRoomDto getChatRoom(Integer chatRoomNo, Integer currentEmpNo) {
        ChatRoomDto chatRoom = chatMapper.getChatRoom(chatRoomNo);
        List<CrEmpDto> participants = chatMapper.getParticipantsByRoom(chatRoomNo)
                                            .stream()
                                            .filter(p -> !p.getEmpNo().equals(currentEmpNo))
                                            .collect(Collectors.toList());

        participants.forEach(participant -> {
            EmployeeDto employee = employeeService.getEmployeeDetailsByEmpNo(currentEmpNo);
            participant.setEmpName(employee.getEmpName());
        });

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

    public void createChatRoom(ChatRoomDto chatRoom, Integer creatorEmpNo) {
        chatMapper.createChatRoom(chatRoom);
         CrEmpDto crEmpDto = CrEmpDto.builder()
                .chatRoomNo(chatRoom.getChatRoomNo())
                .empNo(creatorEmpNo)
                .entryStat(0)
                .build();
         chatMapper.addParticipant(crEmpDto);
    }

    public void addParticipant(CrEmpDto participant) {
        chatMapper.addParticipant(participant);
    }
}
