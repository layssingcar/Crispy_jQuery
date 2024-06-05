package com.mcp.crispy.approval.service;

import com.mcp.crispy.approval.dto.ApplicantDto;
import com.mcp.crispy.approval.mapper.ApprovalMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApprovalService {

    private final ApprovalMapper approvalMapper;

    // 직원 정보 조회
    public ApplicantDto getEmpInfo(int empNo) {
        return approvalMapper.getEmpInfo(empNo);
    }

}
