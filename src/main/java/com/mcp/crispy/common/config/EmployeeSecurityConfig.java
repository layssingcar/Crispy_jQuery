package com.mcp.crispy.common.config;

import com.mcp.crispy.admin.dto.AdminDto;
import com.mcp.crispy.admin.mapper.AdminMapper;
import com.mcp.crispy.common.userdetails.CustomDetails;
import com.mcp.crispy.employee.dto.EmployeeDto;
import com.mcp.crispy.employee.dto.Position;
import com.mcp.crispy.employee.mapper.EmployeeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeSecurityConfig implements UserDetailsService {

    private final AdminMapper adminMapper;
    private final EmployeeMapper employeeMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // 관리자 검색
        AdminDto admin = adminMapper.findByUsername(username);
        if (admin != null) {
            List<SimpleGrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            return new User(admin.getAdminId(), admin.getAdminPw(), authorities);
        }

        // 직원 검색
        EmployeeDto employee = employeeMapper.findByUsername(username).orElseThrow(() ->
                                new UsernameNotFoundException("사용자가 존재하지 않습니다."));
        log.info("FrnNo: {}", employee.getFrnNo());
        log.info("posNo: {}", employee.getPosNo());

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        String rolePrefix = "ROLE_";

        /**
         *  ROLE_ADMIN
         *  ROLE_OWNER
         *  ROLE_MANAGER
         *  ROLE_EMPLOYEE
         */
        Position position = employee.getPosNo();
        switch (position) {
            case OWNER:
                authorities.add(new SimpleGrantedAuthority(rolePrefix + "OWNER"));
                break;
            case MANAGER:
                authorities.add(new SimpleGrantedAuthority(rolePrefix + "MANAGER"));
                break;
            case EMPLOYEE:
                authorities.add(new SimpleGrantedAuthority(rolePrefix + "EMPLOYEE"));
                break;
            default:
                throw new IllegalArgumentException("존재하지 않는 포지션입니다: " + position);
        }
        return new CustomDetails(employee.getEmpId(), employee.getEmpPw(), authorities, employee.getFrnNo());
    }

}
