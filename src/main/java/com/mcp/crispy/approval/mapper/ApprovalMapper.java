package com.mcp.crispy.approval.mapper;

import com.mcp.crispy.approval.dto.ApplicantDto;
import com.mcp.crispy.approval.dto.ApprovalDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ApprovalMapper {

    // 직원 정보 조회
    ApplicantDto getEmpInfo(int empNo);

    // 임시저장 값 존재 여부 확인
    int checkTimeOffTemp(@Param("empNo") int empNo, @Param("timeOffCtNo") int timeOffCtNo);

    // 이전 임시저장 내용 삭제
    int deleteTimeOffTemp(@Param("empNo") int empNo, @Param("timeOffCtNo") int timeOffCtNo);

    // 발주 재고 임시저장
    int insertTimeOffTemp(ApprovalDto approvalDto);
    
    // 임시저장 내용 불러오기
    ApprovalDto getTimeOffTemp(@Param("empNo") int empNo, @Param("timeOffCtNo") int timeOffCtNo);

}
