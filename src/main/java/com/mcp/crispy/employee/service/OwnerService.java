package com.mcp.crispy.employee.service;

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

import java.util.List;

import static com.mcp.crispy.common.utils.RandomCodeUtils.generateTempPassword;

@Slf4j
@Service
@RequiredArgsConstructor
public class OwnerService {

    private final OwnerMapper ownerMapper;
    private final EmailService emailService;
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

        EmployeeRegisterDto employee = EmployeeRegisterDto.builder()
                .empId(employeeRegisterDto.getEmpId())
                .empPw(encodedPassword)
                .empName(employeeRegisterDto.getEmpName())
                .empEmail(employeeRegisterDto.getEmpEmail())
                .empPhone(employeeRegisterDto.getEmpPhone())
                .empStat(EmpStatus.EMPLOYED)
                .empInDt(employeeRegisterDto.getEmpInDt())
                .frnNo(frnNo)
                .posNo(Position.of(employeeRegisterDto.getPosNo().getCode()))
                .build();

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

        OwnerRegisterDto registerDto = OwnerRegisterDto.builder()
                .empId(ownerRegisterDto.getEmpId())
                .empPw(encodedPassword)
                .empName(ownerRegisterDto.getEmpName())
                .empEmail(ownerRegisterDto.getEmpEmail())
                .empPhone(ownerRegisterDto.getEmpPhone())
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
