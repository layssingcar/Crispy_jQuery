package com.mcp.crispy.admin.service;

import com.mcp.crispy.admin.dto.AdminDto;
import com.mcp.crispy.admin.mapper.AdminMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminMapper adminMapper;


    public AdminDto getAdmin(String adminId) {
        return adminMapper.findByUsername(adminId);
    }
}
