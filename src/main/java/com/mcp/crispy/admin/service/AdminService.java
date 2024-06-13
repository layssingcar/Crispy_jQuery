package com.mcp.crispy.admin.service;

import com.mcp.crispy.admin.dto.AdminDto;
import com.mcp.crispy.admin.mapper.AdminMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminMapper adminMapper;

    public AdminDto getAdmin(String adminId) {
        return adminMapper.selectAdmin(adminId);
    }

    // 로그인 시 리프레시 토큰 삽입
    @Transactional
    public void updateRefreshToken(String refreshToken, Integer adminNo) {
        adminMapper.updateRefreshToken(refreshToken, adminNo);
    }

    // 로그아웃 시 리프레시 토큰 삭제
    @Transactional
    public void removeRefreshToken(Integer adminNo) {
        adminMapper.removeRefreshToken(adminNo);
    }
}
