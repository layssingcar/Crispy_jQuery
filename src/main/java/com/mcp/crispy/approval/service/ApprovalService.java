package com.mcp.crispy.approval.service;

import com.mcp.crispy.approval.dto.ApplicantDto;
import com.mcp.crispy.approval.dto.ApprLineDto;
import com.mcp.crispy.approval.dto.ApprovalDto;
import com.mcp.crispy.approval.mapper.ApprovalMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApprovalService {

    private final ApprovalMapper approvalMapper;

    // 직원 정보 조회
    public ApplicantDto getEmpInfo(int empNo) {
        return approvalMapper.getEmpInfo(empNo);
    }

    // 임시저장 값 존재 여부 확인
    public int checkTimeOffTemp(int empNo, int timeOffCtNo) {
        return approvalMapper.checkTimeOffTemp(empNo, timeOffCtNo);
    }

    // 발주 재고 임시저장
    @Transactional
    public int insertTimeOffTemp(ApprovalDto approvalDto) {
        approvalMapper.deleteTimeOffTemp(approvalDto.getEmpNo(), approvalDto.getTimeOffCtNo()); // 이전 임시저장 내용 삭제
        return approvalMapper.insertTimeOffTemp(approvalDto);
    }

    // 임시저장 내용 불러오기
    public ApprovalDto getTimeOffTemp(int empNo, int timeOffCtNo) {
        return approvalMapper.getTimeOffTemp(empNo, timeOffCtNo);
    }

    // 휴가, 휴직 신청
    public int insertTimeOffAppr(ApprovalDto approvalDto) {

        // 전자결재 테이블
        approvalMapper.insertApproval(approvalDto);

        // 문서번호 값 설정 (얕은 복사)
        int apprNo = approvalDto.getApprNo();

        // 휴가,휴직신청서 테이블
        approvalMapper.insertTimeOff(approvalDto);

        // 결재선 목록 가져오기
        List<ApprLineDto> apprLineDtoList = approvalDto.getApprLineDtoList();

        // 결재선 목록 업데이트
        for (int i = 0; i < apprLineDtoList.size(); i++) {
            apprLineDtoList.get(i).setApprLineOrder(i);
            apprLineDtoList.get(i).setApprNo(apprNo);
        }

        // 결재선 테이블
        approvalMapper.insertApprLine(apprLineDtoList);

        return 1;

    }
}
