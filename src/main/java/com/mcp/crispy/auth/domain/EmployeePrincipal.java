package com.mcp.crispy.auth.domain;

import com.mcp.crispy.employee.dto.EmployeeDto;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
public class EmployeePrincipal implements UserDetails {

    private final EmployeeDto employee;
    private final List<GrantedAuthority> authorities;
    private final int frnNo;
    private final int empNo;

    public EmployeePrincipal(EmployeeDto employee, List<GrantedAuthority> authorities) {
        this.employee = employee;
        this.authorities = authorities;
        this.frnNo = employee.getFrnNo();
        this.empNo = employee.getEmpNo();

    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return employee.getEmpPw();
    }

    @Override
    public String getUsername() {
        return employee.getEmpId();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
