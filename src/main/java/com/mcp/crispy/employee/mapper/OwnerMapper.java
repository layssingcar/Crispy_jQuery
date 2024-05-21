package com.mcp.crispy.employee.mapper;

import com.mcp.crispy.employee.dto.EmployeeRegisterDto;
import com.mcp.crispy.employee.dto.OwnerRegisterDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OwnerMapper {

    void insertOwner(OwnerRegisterDto ownerRegisterDto);
    void insertEmployee(EmployeeRegisterDto employee);
}
