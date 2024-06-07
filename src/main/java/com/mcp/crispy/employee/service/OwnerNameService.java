package com.mcp.crispy.employee.service;

import com.mcp.crispy.franchise.dto.FranchiseDto;
import com.mcp.crispy.franchise.mapper.FranchiseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OwnerNameService {

    private final FranchiseMapper franchiseMapper;

    // 대표자명을 직원 번호로 변경
    @Transactional
    public void updateFrnOwnerIfEmployee(Integer empNo, String newEmpName, Integer modifier) {
        FranchiseDto franchiseDto = franchiseMapper.getFrnByEmpNo(empNo);
        if (franchiseDto != null) {
            franchiseMapper.updateFrnOwner(newEmpName, franchiseDto.getFrnNo(), modifier);
        }
    }
}
