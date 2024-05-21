package com.mcp.crispy.common.userdetails;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
public class CustomDetails extends User {
    private final int frnNo;

    public CustomDetails(String username, String password, Collection<? extends GrantedAuthority> authorities, int frnNo) {
        super(username, password, authorities);
        this.frnNo = frnNo;
    }
}
