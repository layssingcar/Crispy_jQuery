package com.mcp.crispy.common.utils;

import com.mcp.crispy.admin.dto.AdminDto;
import com.mcp.crispy.admin.service.AdminService;
import com.mcp.crispy.franchise.dto.FranchiseDto;
import com.mcp.crispy.franchise.service.FranchiseService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FranchiseUtil {

    private final FranchiseService franchiseService;
    private final AdminService adminService;

    public Integer getModifier() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        String role = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authRole -> authRole.startsWith("ROLE_"))
                .findFirst()
                .orElse(null);

        Integer modifier = null;
        if ("ROLE_OWNER".equals(role)) {
            FranchiseDto franchise = franchiseService.getFranchise(username);
            modifier = franchise.getEmpNo();
        } else if ("ROLE_ADMIN".equals(role)) {
            AdminDto admin = adminService.getAdmin(username);
            modifier = admin.getAdminNo();
        }

        if (modifier == null) {
            throw new SecurityException("권한이 없습니다.");
        }
        return modifier;
    }
}
