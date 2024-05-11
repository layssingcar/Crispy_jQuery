package com.mcp.crispy.member.dto;

import lombok.Getter;

@Getter
public enum Role {
	ADMIN("관리자"),
	USER("유저");
	
	private String displayName;
	
	Role(String displayName) {
		this.displayName = displayName;
	}
}
