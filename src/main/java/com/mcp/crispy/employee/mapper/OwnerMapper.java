package com.mcp.crispy.employee.mapper;

import com.mcp.crispy.employee.dto.OwnerRegisterDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OwnerMapper {

    // 점주 등록
    void insertOwner(OwnerRegisterDto ownerRegisterDto);
}
