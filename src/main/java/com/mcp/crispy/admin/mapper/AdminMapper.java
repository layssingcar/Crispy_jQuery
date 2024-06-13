package com.mcp.crispy.admin.mapper;

import com.mcp.crispy.admin.dto.AdminDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AdminMapper {

    // 관리자 등록
    void insertAdmin(AdminDto adminDto);

    // 관리자 정보 찾기
    AdminDto selectAdmin(String username);

    // 리프레시 토큰 삽입
    void updateRefreshToken(@Param("refreshToken") String refreshToken, @Param("adminNo") Integer adminNo);

    // 리프레시 토큰 삭제
    void removeRefreshToken(Integer adminNo);

}
