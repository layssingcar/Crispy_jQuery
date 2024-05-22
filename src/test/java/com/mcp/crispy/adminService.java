package com.mcp.crispy;

import com.mcp.crispy.admin.dto.AdminDto;
import com.mcp.crispy.admin.mapper.AdminMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
public class adminService {

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void 관리자_등록() {
        String password = "1234";
        String encoded = passwordEncoder.encode(password);
        AdminDto adminDto = AdminDto.builder()
                .adminId("admin")
                .adminPw(encoded)
                .build();

        adminMapper.insertAdmin(adminDto);

    }

}
