package com.mcp.crispy.approval.mapper;

import com.mcp.crispy.approval.dto.ApplicantDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ApprovalMapper {

    // 직원 정보 조회
    ApplicantDto getEmpInfo(int empNo);

}
