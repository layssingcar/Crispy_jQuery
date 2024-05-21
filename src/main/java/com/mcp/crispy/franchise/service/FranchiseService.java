package com.mcp.crispy.franchise.service;

import com.mcp.crispy.common.ImageService;
import com.mcp.crispy.employee.dto.OwnerRegisterDto;
import com.mcp.crispy.employee.service.EmployeeService;
import com.mcp.crispy.employee.service.OwnerService;
import com.mcp.crispy.franchise.dto.FranchiseDto;
import com.mcp.crispy.franchise.dto.FranchiseImgDto;
import com.mcp.crispy.franchise.dto.FrnAddressDto;
import com.mcp.crispy.franchise.dto.FrnUpdateDto;
import com.mcp.crispy.franchise.mapper.FranchiseMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class FranchiseService {

    private final FranchiseMapper franchiseMapper;

    private final OwnerService ownerService;
    private EmployeeService employeeService;
    private final ImageService imageService;

    /**
     * 가맹점 및 점주 등록
     *  2024.05.15
     */
    @Async
    @Transactional
    public void registerFranchiseAndOwner(FranchiseDto franchiseDto, OwnerRegisterDto ownerRegisterDto) {
        int frnNo = registerFranchise(franchiseDto);
        log.info("Franchise registered with no {} ", frnNo);
        ownerService.registerOwner(ownerRegisterDto, frnNo, franchiseDto.getFrnOwner());
    }

    @Transactional
    public int registerFranchise(FranchiseDto franchiseDto) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        String startTimeString = franchiseDto.getFrnStartTime().format(formatter);
        String endTimeString = franchiseDto.getFrnEndTime().format(formatter);

        FranchiseDto franchise = FranchiseDto.builder()
                .frnName(franchiseDto.getFrnName())
                .frnOwner(franchiseDto.getFrnOwner())
                .frnTel(franchiseDto.getFrnTel())
                .frnZip(franchiseDto.getFrnZip())
                .frnStreet(franchiseDto.getFrnStreet())
                .frnDetail(franchiseDto.getFrnDetail())
                .frnStartTime(LocalTime.parse(startTimeString))
                .frnEndTime(LocalTime.parse(endTimeString))
                .frnJoinDt(LocalDateTime.now())
                .build();

        franchiseMapper.insertFranchise(franchise);

        return franchise.getFrnNo();
    }

    public FranchiseDto getFranchise(String empId) {
         return franchiseMapper.findByEmpId(empId);
    }


    @Transactional
    public void insertOrUpdateFrnImg(Integer frnNo,
                                         MultipartFile file) throws IOException {
        if(file != null && !file.isEmpty()) {
            String storedProfileImage = imageService.storeProfileImage(file);
            String storedUrl = "/franchise/" + storedProfileImage;
            FranchiseImgDto franchiseImgDto = FranchiseImgDto.builder()
                    .frnNo(frnNo)
                    .frnImg(storedUrl)
                    .modifyDt(Date.from(Instant.now()))
                    .build();
            franchiseMapper.insertOrUpdateFrnImg(franchiseImgDto);
        }
    }

    @Transactional
    public void changeFrnTel(String frnTel, Integer frnNo, Integer modifier) {
        if (frnNo == null) {
            throw new IllegalArgumentException("해당하는 가맹점이 존재하지 않습니다.");
        }
        franchiseMapper.updateFrnTel(frnTel, frnNo, modifier);
    }

    @Transactional
    public void insertOrUpdateFrnAddress(FrnAddressDto frnAddressDto, Integer modifier) {
        franchiseMapper.insertOrUpdateFrnAddress(frnAddressDto, modifier);
    }

    // 대표자명 변경
    @Transactional
    public void changeFrnOwner(String frnOwner, Integer modifier, Integer frnNo) {
        franchiseMapper.updateFrnOwner(frnOwner, modifier, frnNo);
    }

    // 운영시간 변경
    @Transactional
    public void changeOperatingTime(FrnUpdateDto frnUpdateDto, Integer modifier) {
        franchiseMapper.updateOperatingTime(frnUpdateDto.getFrnStartTime(), frnUpdateDto.getFrnEndTime(), modifier, frnUpdateDto.getFrnNo());
    }
}
