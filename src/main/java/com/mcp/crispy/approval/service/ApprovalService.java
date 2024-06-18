package com.mcp.crispy.approval.service;

import com.mcp.crispy.approval.dto.*;
import com.mcp.crispy.approval.mapper.ApprovalMapper;
import com.mcp.crispy.common.ImageService;
import com.mcp.crispy.common.page.PageResponse;
import com.mcp.crispy.common.utils.MyFileUtils;
import com.mcp.crispy.employee.dto.EmployeeDto;
import com.mcp.crispy.employee.service.EmployeeService;
import com.mcp.crispy.notification.dto.NotifyCt;
import com.mcp.crispy.notification.dto.NotifyDto;
import com.mcp.crispy.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApprovalService {

    private final ApprovalMapper approvalMapper;
    private final EmployeeService employeeService;
    private final NotificationService notificationService;
    private final ImageService imageService;
    private final MyFileUtils myFileUtils;

//    @Value("${file.appr-dir.window}")
    private String forderPath;

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
    public int insertTimeOffAppr(ApprovalDto approvalDto) throws IOException {

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

        // 선택한 파일 X
        if (approvalDto.getApprFile() == null
                || approvalDto.getApprFile().isEmpty()
                || approvalDto.getApprFile().get(0).isEmpty())
            return 1;

        List<ApprFileDto> fileDtoList = new ArrayList<>();

        for (MultipartFile file : approvalDto.getApprFile()) {

            String apprOrigin = file.getOriginalFilename();
            String apprRename = myFileUtils.getBoardRename(apprOrigin);
            String apprPath = "/appr_file/";

            ApprFileDto fileDto = ApprFileDto.builder()
                    .apprOrigin(apprOrigin)
                    .apprRename(apprRename)
                    .apprPath(apprPath)
                    .apprNo(apprNo)
                    .apprFile(file)
                    .build();

            fileDtoList.add(fileDto);

        }

        // 첨부파일 테이블
        int result = approvalMapper.insertApprFile(fileDtoList);

        if (result < fileDtoList.size())
            throw new RuntimeException("전자 결재 파일 삽입 실패");

        // 서버에 첨부파일 저장
        for (ApprFileDto fileDto : fileDtoList) {
            String rename = fileDto.getApprRename();
            String path = forderPath;
            fileDto.getApprFile().transferTo(new File(path + rename));
        }

        // 첫 번째 결재자에게 알림 전송
        if (!apprLineDtoList.isEmpty()) {
            ApprLineDto apprLineDto = apprLineDtoList.get(0);
            log.info("첫번째 결재자: {}", apprLineDto);
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

    // 결재 문서 상세 조회 (발주 신청서)
    public ApprovalDto getStockOrderApprDetail(int apprNo) {
        return approvalMapper.getStockOrderApprDetail(apprNo);
    }

    // 문서 결재
    @Transactional
    public int changeApprLineStat(Map<String, Object> map) throws IOException {

        int apprLineStat = Integer.parseInt(map.get("apprLineStat").toString());
        int empNo = Integer.parseInt(map.get("empNo").toString());
        int apprNo = Integer.parseInt(map.get("apprNo").toString());
        String apprType = map.get("apprType").toString(); // 결재 문서 타입 추가
     
        // 결재 상태가 승인(1)인 경우 서명 데이터를 저장
        if (apprType.equals("time-off") && apprLineStat == 1) {
            String signData = map.get("data").toString();
            String fileName = imageService.storeSignatureImage(signData, empNo);
            String path = "/emp_sign/";
            map.put("data", path + fileName);
        }

        // 결재 상태 업데이트
        int result = approvalMapper.changeApprLineStat(map);

        // 결재선 목록 가져오기
        List<ApprLineDto> detailApprLine = approvalMapper.getDetailApprLine(apprNo);
        log.info("detailApprLine: {}", detailApprLine);
        NotifyCt notifyCt;
        ApprovalDto approvalDetail;

        // 결재 문서 타입에 따라 상세 정보 및 알림 카테고리 설정
        if (apprType.equals("time-off")) {
            approvalDetail = approvalMapper.getTimeOffApprDetail(empNo, apprNo);
            notifyCt = mapTimeOffCtToNotifyCt(approvalDetail.getTimeOffCtNo());
        } else if (apprType.equals("stock-order")) {
            approvalDetail = approvalMapper.getStockOrderApprDetail(apprNo);
            notifyCt = NotifyCt.ORDER;
        } else {
            throw new IllegalArgumentException("알 수 없는 결재 타입입니다: " + apprType);
        }

        // 결재 상태가 승인(1)인 경우
        if (apprLineStat == 1) { // 승인
            if (detailApprLine.size() == 1) {
                // 결재자가 1명인 경우 바로 최종 승인 알림 전송
                int creatorEmpNo = detailApprLine.get(0).getCreator();
                NotifyDto notifyDto = NotifyDto.builder()
                        .notifyCt(NotifyCt.FINAL_APPROVAL)
                        .notifyContent("결재가 승인 되었습니다.")
                        .build();
                notificationService.sendApprovalNotification(notifyDto, creatorEmpNo);
                approvalDetail.setApprStat(ApprStat.APPROVING.getCode());
                approvalMapper.updateApprovalStat(approvalDetail.getApprStat(), approvalDetail.getApprNo()); // DB 업데이트
                // 결재자가 여러명인 경우 다음 결재자에게 알림 전송
            } else if (!detailApprLine.isEmpty()) {
                ApprLineDto nextApprLineDto = detailApprLine.get(0);
                EmployeeDto employee = employeeService.getEmployeeDetailsByEmpNo(nextApprLineDto.getCreator());
                NotifyDto notifyDto = NotifyDto.builder()
                        .notifyCt(NotifyCt.APPROVAL)
                        .notifyContent(employee.getEmpName() + "님이 " + notifyCt.getDescription() + "결재를 요청했습니다.") // creator
                        .build();
                log.info("changeApprLineStat: {}", notifyDto);
                notificationService.sendApprovalNotification(notifyDto, empNo);
                approvalDetail.setApprStat(ApprStat.ONGOING.getCode());
                approvalMapper.updateApprovalStat(approvalDetail.getApprStat(), approvalDetail.getApprNo());
            }

            // 모든 결재자가 승인했는지 확인
            boolean allApproved = detailApprLine.stream()
                    .allMatch(line -> line.getApprLineStat() == 1);

            // 모든 결재자가 승인한 경우 최종 승인 알림 전송
            if (allApproved) {
                int creatorEmpNo = detailApprLine.get(0).getCreator();
                log.info("creatorEmpNo: {}", creatorEmpNo);
                NotifyDto notifyDto = NotifyDto.builder()
                        .notifyCt(NotifyCt.FINAL_APPROVAL)
                        .notifyContent("결재가 승인 되었습니다.")
                        .build();
                notificationService.sendApprovalNotification(notifyDto, creatorEmpNo);
                approvalDetail.setApprStat(ApprStat.APPROVING.getCode());
                approvalMapper.updateApprovalStat(approvalDetail.getApprStat(), approvalDetail.getApprNo()); // DB 업데이트
            }
        }

        // 반려 시 결재 올린 직원에게 반려 알림 전송
        if (apprLineStat == 2) {
            int creatorEmpNo = detailApprLine.get(0).getCreator();
            NotifyDto notifyDto = NotifyDto.builder()
                    .notifyCt(NotifyCt.REJECTION)
                    .notifyContent("결재가 반려되었습니다.")
                    .build();
            notificationService.sendApprovalNotification(notifyDto, creatorEmpNo);
            approvalDetail.setApprStat(ApprStat.RETURNING.getCode());
            approvalMapper.updateApprovalStat(approvalDetail.getApprStat(), approvalDetail.getApprNo()); // DB 업데이트
        }
        return result;
    }

}
