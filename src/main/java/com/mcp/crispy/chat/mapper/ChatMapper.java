package com.mcp.crispy.chat.mapper;

import com.mcp.crispy.chat.dto.ChatMessageDto;
import com.mcp.crispy.chat.dto.ChatRoomDto;
import com.mcp.crispy.chat.dto.CrEmpDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ChatMapper {
    List<ChatRoomDto> getChatRooms(Integer empNo);
    ChatRoomDto getChatRoom(Integer chatRoomNo);
    List<ChatMessageDto> getMessages(Integer chatRoomNo);
    void saveMessage(ChatMessageDto chatMessageDto);
    void createChatRoom(ChatRoomDto chatRoomDto);
    void addParticipant(CrEmpDto crEmpDto);
    List<CrEmpDto> getParticipantsByRoom(Integer chatRoomNo);
}
