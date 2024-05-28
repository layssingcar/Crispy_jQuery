package com.mcp.crispy.chat.mapper;

import com.mcp.crispy.chat.dto.ChatMessageDto;
import com.mcp.crispy.chat.dto.ChatRoomDto;
import com.mcp.crispy.chat.dto.CrEmpDto;
import com.mcp.crispy.chat.dto.UnreadMessageCountDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ChatMapper {
    // 채팅방 목록
    List<ChatRoomDto> getChatRooms(@Param("empNo")Integer empNo);

    // 채팅방 내부 ( 참가자 )
    ChatRoomDto getChatRoom(@Param("chatRoomNo") Integer chatRoomNo);

    // 채팅방 내에 메시지 목록
    List<ChatMessageDto> getMessages(@Param("chatRoomNo") Integer chatRoomNo, @Param("empNo") Integer empNo);

    // 메시지 저장
    void saveMessage(ChatMessageDto chatMessageDto);

    // 채팅방 만들기
    void createChatRoom(ChatRoomDto chatRoomDto);

    CrEmpDto getParticipant(@Param("chatRoomNo") Integer chatRoomNo, @Param("empNo") Integer empNo);

    // 유저 초대
    void addParticipant(CrEmpDto crEmpDto);

    // 채팅방 초대할 때 사용하는 메소드
    void updateParticipantEntryStat(CrEmpDto crEmpDto);

    // 채팅방 나갈 때 사용하는 메소드
    void updateEntryStat(CrEmpDto crEmpDto);

    List<CrEmpDto> getParticipantsByRoom(Integer chatRoomNo);

    // 채팅방 접속 기록 (최초 삽입, 이후 수정)
    void addAccessRecord(@Param("chatRoomNo") Integer chatRoomNo, @Param("empNo") Integer empNo);
    void updateAccessRecord(@Param("chatRoomNo") Integer chatRoomNo, @Param("empNo") Integer empNo);
    // 접속 기록 체크
    Integer checkAccessExists(@Param("chatRoomNo") Integer chatRoomNo, @Param("empNo") Integer empNo);

    // 입장 기록
    void insertEntryRecord(@Param("chatRoomNo") Integer chatRoomNo, @Param("empNo") Integer empNo);
    void updateExitRecord(@Param("chatRoomNo") Integer chatRoomNo, @Param("empNo") Integer empNo);
    void updateEntryRecord(@Param("chatRoomNo") Integer chatRoomNo, @Param("empNo") Integer empNo);
    Integer checkEntryRecordExists(@Param("chatRoomNo") Integer chatRoomNo, @Param("empNo") Integer empNo);

    // 안 읽은 메시지 개수 반환
    List<UnreadMessageCountDto> countUnreadMessages(@Param("empNo") Integer empNo);

    // 알림 상태 토글
    void toggleAlarmStat(@Param("chatRoomNo") Integer chatRoomNo, @Param("empNo") Integer empNo);
}
