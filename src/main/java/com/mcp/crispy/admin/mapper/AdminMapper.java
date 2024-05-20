package com.mcp.crispy.admin.mapper;

import com.mcp.crispy.admin.dto.AdminDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AdminMapper {

//    void insertAdmin(AdminDto adminDto);

    @Select("SELECT ADMIN_ID, ADMIN_PW FROM ADMIN_T WHERE ADMIN_ID = #{adminId}")
    AdminDto findByUsername(String username);

}
