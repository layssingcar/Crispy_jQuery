package com.mcp.crispy.notification.mapper;


import com.mcp.crispy.notification.dto.NotifyDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NotificationMapper {

    // 알림 생성
    void insertNotification(NotifyDto notifyDto);

    // 알림 조회
    List<NotifyDto> selectNotify(@Param("empNo") int empNo);

    // 안 읽은 알림 조회
    List<NotifyDto> selectUnreadNotify(@Param("empNo") int empNo);

    // 안 읽은 알림 개수
    int countUnreadNotify(@Param("empNo") int empNo);

    // 읽음 상태 수정
    void updateNotifyToRead(@Param("notifyNo") int notifyNo);
}
