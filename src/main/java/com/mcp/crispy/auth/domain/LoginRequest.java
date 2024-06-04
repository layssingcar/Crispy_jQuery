package com.mcp.crispy.auth.domain;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
