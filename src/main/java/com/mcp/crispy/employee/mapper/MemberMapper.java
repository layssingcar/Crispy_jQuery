package com.mcp.crispy.employee.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.mcp.crispy.employee.dto.MemberDto;


@Mapper
public interface MemberMapper {

	
	MemberDto findByMemberEmail(String userEmail);
}
