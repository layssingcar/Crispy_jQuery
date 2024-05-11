package com.mcp.crispy.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class MemberDto {

	private int userNo;
	private String userEmail;
	private String userPw;
	private String userName;
	private Role role;
	
	
}
