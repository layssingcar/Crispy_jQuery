package com.mcp.crispy.admin.mapper;

import com.mcp.crispy.admin.dto.AdminDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AdminMapper {

    // 관리자 등록
    void insertAdmin(AdminDto adminDto);

    // 관리자 정보 찾기
    AdminDto selectAdmin(String username);

}
