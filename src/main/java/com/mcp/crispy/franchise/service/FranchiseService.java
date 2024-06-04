package com.mcp.crispy.franchise.service;

import com.mcp.crispy.common.ImageService;
import com.mcp.crispy.employee.dto.OwnerRegisterDto;
import com.mcp.crispy.employee.service.EmployeeService;
import com.mcp.crispy.employee.service.OwnerService;
import com.mcp.crispy.franchise.dto.FranchiseDto;
import com.mcp.crispy.franchise.dto.FranchiseRegisterDto;
import com.mcp.crispy.franchise.dto.FrnAddressUpdateDto;
import com.mcp.crispy.franchise.dto.FrnUpdateDto;
import com.mcp.crispy.franchise.mapper.FranchiseMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FranchiseService {

    private final FranchiseMapper franchiseMapper;
    private final EmployeeService employeeService;
    private final OwnerService ownerService;
    private final ImageService imageService;

    // 가맹점 및 점주 등록
    @Async
    @Transactional
    public void registerFranchiseAndOwner(FranchiseRegisterDto franchiseRegisterDto, OwnerRegisterDto ownerRegisterDto) {
        int frnNo = registerFranchise(franchiseRegisterDto);
        log.info("Franchise registered with no {} ", frnNo);
        ownerService.registerOwner(ownerRegisterDto, frnNo, franchiseRegisterDto.getFrnOwner());
    }

    // 가맹점 등록
    @Transactional
    public int registerFranchise(FranchiseRegisterDto franchiseRegisterDto) {
        FranchiseRegisterDto franchise = FranchiseRegisterDto.builder()
                .frnName(franchiseRegisterDto.getFrnName())
                .frnOwner(franchiseRegisterDto.getFrnOwner())
                .frnTel(franchiseRegisterDto.getFrnTel())
                .frnZip(franchiseRegisterDto.getFrnZip())
                .frnStreet(franchiseRegisterDto.getFrnStreet())
                .frnDetail(franchiseRegisterDto.getFrnDetail())
                .frnStartTime(franchiseRegisterDto.getFrnStartTime())
                .frnEndTime(franchiseRegisterDto.getFrnEndTime())
                .build();

        franchiseMapper.insertFranchise(franchise);

        return franchise.getFrnNo();
    }

    public FranchiseDto getFranchise(String empId) {
        if(empId.equals("admin")) {
            return handleAdminAccess();
        }
         return franchiseMapper.findByEmpId(empId);
    }

    private FranchiseDto handleAdminAccess() {
        return new FranchiseDto();
    }

    // 가맹점 번호 수정
    @Transactional
    public void changeFrnTel(String frnTel, Integer frnNo, Integer modifier) {
        if (frnNo == null) {
            throw new IllegalArgumentException("해당하는 가맹점이 존재하지 않습니다.");
        }
        franchiseMapper.updateFrnTel(frnTel, frnNo, modifier);
    }

    // 가맹점 사진 수정
    @Transactional
    public void updateFrnImg(Integer frnNo,
                             MultipartFile file, Integer modifier) throws IOException {
        int count = franchiseMapper.countByFrnNo(frnNo);

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("가맹점 이미지 파일이 없습니다.");
        } else {
            String storedProfileImage = imageService.storeProfileImage(file);
            String storedUrl = "/franchise/" + storedProfileImage;

            if (count > 0) {
                franchiseMapper.updateFrnImg(storedUrl, frnNo, modifier);
            } else {
                throw new IllegalArgumentException("가맹점이 존재하지 않습니다.");
            }
        }
    }

    // 주소 변경
    @Transactional
    public void updateFrnAddress(FrnAddressUpdateDto frnAddressUpdateDto, Integer modifier) {
        int frnNo = frnAddressUpdateDto.getFrnNo();
        int count = franchiseMapper.countByFrnNo(frnNo);

        if (count > 0) {
            franchiseMapper.updateFrnAddress(frnAddressUpdateDto, modifier);
        } else {
            throw new IllegalArgumentException("가맹점이 존재하지 않습니다.");
        }
    }

    // 대표자명 변경
    @Transactional
    public void changeFrnOwner(String frnOwner, Integer empNo, Integer modifier, Integer frnNo) {
        int count = franchiseMapper.countByFrnNo(frnNo);
        if (count > 0) {
            employeeService.changeEmpName(frnOwner, empNo, modifier);
            franchiseMapper.updateFrnOwner(frnOwner, frnNo, modifier);
        } else {
            throw new IllegalArgumentException("가맹점이 존재하지 않습니다.");
        }
    }

    // 운영시간 변경
    @Transactional
    public void changeOperatingTime(FrnUpdateDto frnUpdateDto, Integer modifier) {
        Integer frnNo = frnUpdateDto.getFrnNo();
        int count = franchiseMapper.countByFrnNo(frnNo);

        if (count > 0) {
            String frnStartTime = frnUpdateDto.getFrnStartTime();
            String frnEndTime = frnUpdateDto.getFrnEndTime();

            if (isValidTimeRange(frnStartTime, frnEndTime)) {
                throw new IllegalArgumentException("종료 시간이 시작 시간보다 빠르거나 같을 수 없습니다.");
            }

            franchiseMapper.updateOperatingTime(frnUpdateDto.getFrnStartTime(), frnUpdateDto.getFrnEndTime(), modifier, frnUpdateDto.getFrnNo());
        } else {
            throw new IllegalArgumentException("가맹점이 존재하지 않습니다.");
        }
    }

    private boolean isValidTimeRange(String frnStartTime, String frnEndTime) {
        LocalTime start = LocalTime.parse(frnStartTime);
        LocalTime end = LocalTime.parse(frnEndTime);

        // 종료 시간이 시작 시간보다 빠르거나 같은지 여부를 반환
        return !end.isAfter(start);
    }

    public List<FranchiseDto> getFranchiseList() {
        return franchiseMapper.getFranchiseList();
    }

    public FranchiseDto getFrnDetailsByFrnNo(Integer frnNo) {
        return franchiseMapper.findFrnDetailsByFrnNo(frnNo)
                .orElseThrow(() -> new IllegalArgumentException("해당 가맹점이 존재하지 않습니다."));
    }

    // 폼 수정 메소드
    @Transactional
    public void updateFormFrn(FrnUpdateDto frnUpdateDto, Integer modifier) {
        franchiseMapper.updateFormFranchise(frnUpdateDto, modifier);
    }
}
