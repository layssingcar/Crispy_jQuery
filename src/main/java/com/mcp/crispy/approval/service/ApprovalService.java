package com.mcp.crispy.approval.service;

import com.mcp.crispy.approval.dto.*;
import com.mcp.crispy.approval.mapper.ApprovalMapper;
import com.mcp.crispy.common.page.PageResponse;
import com.mcp.crispy.employee.dto.EmployeeDto;
import com.mcp.crispy.employee.service.EmployeeService;
import com.mcp.crispy.notification.dto.NotifyCt;
import com.mcp.crispy.notification.dto.NotifyDto;
import com.mcp.crispy.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApprovalService {

    private final ApprovalMapper approvalMapper;
    private final EmployeeService employeeService;
    private final NotificationService notificationService;

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

    // 결재선 불러오기
    public List<ApprLineDto> getApprLine(int frnNo, int empNo) {
        return approvalMapper.getApprLine(frnNo, empNo);
    }

    // 휴가, 휴직 신청
    @Transactional
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
            apprLineDtoList.get(i).setCreator(approvalDto.getEmpNo());
        }

        // 결재선 테이블
        approvalMapper.insertApprLine(apprLineDtoList);

        // 첫 번째 결재자에게 알림 전송
        if (!apprLineDtoList.isEmpty()) {
            ApprLineDto apprLineDto = apprLineDtoList.get(apprLineDtoList.size() - 1);
            EmployeeDto employee = employeeService.getEmployeeDetailsByEmpNo(approvalDto.getEmpNo());
            NotifyCt notifyCt = mapTimeOffCtToNotifyCt(approvalDto.getTimeOffCtNo());
            NotifyDto notifyDto = NotifyDto.builder()
                    .notifyCt(notifyCt)
                    .notifyContent(employee.getEmpName() + "님이 " + notifyCt.getDescription() + "결재를 요청했습니다.") // creator
                    .build();

            // 알림 전송
            notificationService.sendApprovalNotification(notifyDto, apprLineDto.getEmpNo());
        }

        return 1;

    }

    // TimeOffCtNo랑 NotifyCy 매핑 하는 메소드
    private NotifyCt mapTimeOffCtToNotifyCt(int timeOffCtNo) {
        return switch (timeOffCtNo) {
            case 0 -> NotifyCt.VACATION;
            case 1 -> NotifyCt.LEAVE_OF_ABSENCE;
            default -> throw new IllegalArgumentException("올바르지 않은 카테고리 번호입니다: " + timeOffCtNo);
        };
    }

    // 결재 문서 조회 (기안함, 결재함)
    public PageResponse<ApprovalDto> getTimeOffApprList(ApprOptionDto apprOptionDto, int limit) {

        int page = Math.max(apprOptionDto.getPageNo(), 1);
        int totalCount = approvalMapper.getTimeOffApprCount(apprOptionDto);
        int totalPage = totalCount / limit + ((totalCount % limit > 0) ? 1 : 0);
        int startPage = Math.max(page - 2, 1);
        int endPage = Math.min(page + 2,  totalPage);

        RowBounds rowBounds = new RowBounds(limit * (page - 1), limit);
        List<ApprovalDto> items = approvalMapper.getTimeOffApprList(apprOptionDto, rowBounds);

        for (ApprovalDto approvalDto : items) {

            // 문서상태명 설정 (대기, 진행중, 승인, 반려)
            int apprStat = approvalDto.getApprStat();
            String apprStatName = ApprStat.of(apprStat).getDesciption();
            approvalDto.setApprStatName(apprStatName);

            // 문서카테고리명 설정 (휴가신청서, 휴직신청서)
            int timeOffCtNo = approvalDto.getTimeOffCtNo();
            String timeOffCtName = TimeOffCtNo.of(timeOffCtNo).getDesciption();
            approvalDto.setTimeOffCtName(timeOffCtName);

        }

        return new PageResponse<>(items, totalPage, startPage, endPage, page);

    }

    // 결재 문서 상세 조회 (휴가,휴직 신청서)
    public ApprovalDto getTimeOffApprDetail(int empNo, int apprNo) {
        return approvalMapper.getTimeOffApprDetail(empNo, apprNo);
    }

}
