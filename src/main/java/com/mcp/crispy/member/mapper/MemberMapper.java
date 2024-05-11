package com.mcp.crispy.member.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.mcp.crispy.member.dto.MemberDto;


@Mapper
public interface MemberMapper {

	
	MemberDto findByMemberEmail(String userEmail);
}
