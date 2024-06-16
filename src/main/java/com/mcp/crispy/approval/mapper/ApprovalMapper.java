package com.mcp.crispy.approval.mapper;

import com.mcp.crispy.approval.dto.ApplicantDto;
import com.mcp.crispy.approval.dto.ApprLineDto;
import com.mcp.crispy.approval.dto.ApprOptionDto;
import com.mcp.crispy.approval.dto.ApprovalDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;
import java.util.Map;

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

    // 결재선 불러오기
    List<ApprLineDto> getApprLine(@Param("frnNo") int frnNo, @Param("empNo") int empNo);

    // 휴가,휴직 신청 (전자결재 테이블)
    int insertApproval(ApprovalDto approvalDto);

    // 휴가,휴직 신청 (휴가,휴직신청서 테이블)
    int insertTimeOff(ApprovalDto approvalDto);

    // 휴가,휴직 신청 (결재선 테이블)
    int insertApprLine(List<ApprLineDto> apprLineDtoList);

    // 결재 문서 목록 수 조회 (휴가,휴직 신청서)
    int getTimeOffApprCount(ApprOptionDto apprOptionDto);

    // 결재 문서 목록 조회 (휴가,휴직 신청서)
    List<ApprovalDto> getTimeOffApprList(ApprOptionDto apprOptionDto, RowBounds rowBounds);

    // 결재 문서 상세 조회 (휴가,휴직 신청서)
    ApprovalDto getTimeOffApprDetail(@Param("empNo") int empNo, @Param("apprNo") int apprNo);

    // 결재 문서 상세 조회 (발주 신청서)
    ApprovalDto getStockOrderApprDetail(int apprNo);

    // 문서 결재
    int changeApprLineStat(Map<String, Object> map);

}
