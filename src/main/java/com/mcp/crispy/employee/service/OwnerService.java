package com.mcp.crispy.employee.service;

import com.mcp.crispy.common.ImageService;
import com.mcp.crispy.common.exception.EmployeeNotFoundException;
import com.mcp.crispy.common.userdetails.CustomDetails;
import com.mcp.crispy.email.service.EmailService;
import com.mcp.crispy.employee.dto.*;
import com.mcp.crispy.employee.mapper.EmployeeMapper;
import com.mcp.crispy.employee.mapper.OwnerMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

import static com.mcp.crispy.common.utils.RandomCodeUtils.generateTempPassword;

@Slf4j
@Service
@RequiredArgsConstructor
public class OwnerService {

    private final OwnerMapper ownerMapper;
    private final EmailService emailService;
    private final ImageService imageService;
    private final PasswordEncoder passwordEncoder;
    private final EmployeeMapper employeeMapper;


    /**
     * 2024.05.16
     * 직원 등록
     * @param employeeRegisterDto
     * @param authentication
     */
    @Transactional
    public void registerEmployee(EmployeeRegisterDto employeeRegisterDto, Authentication authentication) {
        String tempPassword = generateTempPassword();
        String encodedPassword = passwordEncoder.encode(tempPassword);

        CustomDetails userDetails = (CustomDetails) authentication.getPrincipal();
        int frnNo = userDetails.getFrnNo();

        String storedUrl;
        try {
            String storedProfileImage = imageService.storeDefaultProfileImage();
            storedUrl = "/profiles/" + storedProfileImage; // 기본 프로필 이미지 저장

        } catch (IOException e) {
            throw new IllegalStateException("기본 프로필 이미지를 저장하는 중 오류가 발생했습니다.", e);
        }
        employeeRegisterDto.setEmpProfile(storedUrl);
        EmployeeRegisterDto employee = EmployeeRegisterDto.builder()
                .empId(employeeRegisterDto.getEmpId())
                .empPw(encodedPassword)
                .empName(employeeRegisterDto.getEmpName())
                .empEmail(employeeRegisterDto.getEmpEmail())
                .empPhone(employeeRegisterDto.getEmpPhone())
                .empProfile(employeeRegisterDto.getEmpProfile())
                .empStat(EmpStatus.EMPLOYED)
                .empInDt(employeeRegisterDto.getEmpInDt())
                .frnNo(frnNo)
                .posNo(Position.of(employeeRegisterDto.getPosNo().getCode()))
                .build();
        log.info("이미지: {}", employeeRegisterDto.getEmpProfile());
        log.info("EmployeeRegisterDto: {}", employee); // DTO 객체 로깅
        employeeMapper.insertEmployee(employee);

        emailService.sendTempPasswordEmail(employee.getEmpEmail(), tempPassword);
    }

    /**
     * 2024.05.16
     * 점주 등록
     * @param ownerRegisterDto
     * @param frnNo
     * @param frnOwner
     * @return registerDto.getEmpNo
     */
    @Transactional
    public int registerOwner(OwnerRegisterDto ownerRegisterDto, int frnNo, String frnOwner) {
        String tempPassword = generateTempPassword();
        String encodedPassword = passwordEncoder.encode(tempPassword);
        ownerRegisterDto.setEmpPw(encodedPassword);
        ownerRegisterDto.setFrnNo(frnNo);
        ownerRegisterDto.setEmpName(frnOwner);

        try {
            String storedProfileImage = imageService.storeDefaultProfileImage();
            String storedUrl = "/profiles/" + storedProfileImage;
            ownerRegisterDto.setEmpProfile(storedUrl);
        } catch (IOException e) {
            throw new IllegalStateException("기본 프로필 이미지를 저장하는 중 오류가 발생했습니다.", e);
        }

        OwnerRegisterDto registerDto = OwnerRegisterDto.builder()
                .empId(ownerRegisterDto.getEmpId())
                .empPw(encodedPassword)
                .empName(ownerRegisterDto.getEmpName())
                .empEmail(ownerRegisterDto.getEmpEmail())
                .empPhone(ownerRegisterDto.getEmpPhone())
                .empProfile(ownerRegisterDto.getEmpProfile())
                .posNo(Position.OWNER.getCode())
                .frnNo(ownerRegisterDto.getFrnNo())
                .build();
        log.info("registerDto, frn: {}", frnNo);
        log.info("registerDto, posNo: {}", registerDto.getPosNo());
        ownerMapper.insertOwner(ownerRegisterDto);
        emailService.sendTempPasswordEmail(ownerRegisterDto.getEmpEmail(), tempPassword);
        return registerDto.getEmpNo();
    }

    public List<EmployeeDto> getEmployeesByFrnNo(int frnNo) {
        List<EmployeeDto> employeeByFranchise = employeeMapper.findEmployeeByFranchise(frnNo);
        if(employeeByFranchise.isEmpty()) {
            throw new EmployeeNotFoundException("error", "아직 등록된 사원이 없습니다.");
        }
        return employeeByFranchise;
    }
}
