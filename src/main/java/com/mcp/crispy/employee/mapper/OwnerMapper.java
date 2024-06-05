package com.mcp.crispy.employee.mapper;

import com.mcp.crispy.employee.dto.OwnerRegisterDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OwnerMapper {

    // 점주 등록
    void insertOwner(OwnerRegisterDto ownerRegisterDto);

    // 직원 삭제
    void deleteEmployee(Integer empNo);

    // 직원 선택 삭제
    void deleteEmployees(List<Integer> empNos);

    // 번호에 맞는 직원 호출
    int countByEmpNo(Integer empNo);
}
