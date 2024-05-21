package com.mcp.crispy.employee.service;

import com.mcp.crispy.common.ImageService;
import com.mcp.crispy.email.service.EmailService;
import com.mcp.crispy.employee.dto.*;
import com.mcp.crispy.employee.mapper.EmployeeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;

import static com.mcp.crispy.common.utils.RandomCodeUtils.generateTempPassword;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeMapper employeeMapper;
    private final EmailService emailService;
    private final ImageService imageService;
    private final PasswordEncoder passwordEncoder;


    public EmployeeDto getEmployeeName(String username) {
        return employeeMapper.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("직원이 존재하지 않습니다."));
    }

    public EmployeeDto getEmployeeDetailsByEmpNo(Integer empNo) {
        return employeeMapper.findByEmployeeDetailsByEmpNo(empNo)
                .orElseThrow(() -> new UsernameNotFoundException("직원이 존재하지 않습니다."));
    }

    public FindEmployeeDto getEmpEmail(String empEmail, String empName) {
        return employeeMapper.findByEmpEmail(empEmail, empName)
                .orElseThrow(() -> new UsernameNotFoundException("이메일이 존재하지 않습니다."));
    }

    // 비밀번호 변경 ( 자신이 변경하는 거 )
    @Transactional
    public void updateEmployeePassword(String empId, PasswordChangeDto passwordChangeDto) {
        EmployeeDto employee = employeeMapper.findByUsername(empId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

//        validatePassword(passwordChangeDto, employee);

        String encodedPassword = passwordEncoder.encode(passwordChangeDto.getNewPassword());
        employeeMapper.updateEmpPw(empId, encodedPassword);
    }

    // 임시 비밀번호로 변경 ( 관리자가 변경해주는 거)
    @Transactional
    public void resetEmployeePassword(String email, String empName) {
        FindEmployeeDto employee = employeeMapper.findByEmpEmail(email, empName)
                .orElseThrow(() -> new UsernameNotFoundException("이메일이 존재하지 않습니다."));
        log.info("employee: {}", employee.getEmpEmail());
        String tempPassword = generateTempPassword();
        String encodedPassword = passwordEncoder.encode(tempPassword);
        employee.setEmpPw(encodedPassword);
        employeeMapper.updateEmpPw(employee.getEmpId(), encodedPassword);
        emailService.sendTempPasswordEmail(email, tempPassword);
    }


    /**
     * 파라미터에 empId가 존재하면 비밀번호 찾기
     * empId가 존재하지 않으면 아이디 찾기
     * @param empName
     * @param empEmail
     * @param empId
     * @return
     */
    public boolean checkEmployeeExists(String empName, String empEmail, String empId) {
        if (empId != null) {
            return employeeMapper.findByEmpNameAndEmpEmailAndEmpId(empName, empEmail, empId).isPresent();
        } else {
            return employeeMapper.findByEmpNameAndEmpEmail(empName, empEmail).isPresent();
        }
    }

    /**
     * 주소가 존재하면 업데이트
     * 주소가 존재하지 않으면 정보 삽입
     * @param empAddressDto
     */
    @Transactional
    public void insertOrUpdateAddress(EmpAddressDto empAddressDto) {
        employeeMapper.insertOrUpdateAddress(empAddressDto);
    }

    /**
     * 서명이 존재하면 업데이트
     * 서명이 존재하지 않으면 삽입
     * @param employeeSignDto
     */
    @Transactional
    public void insertOrUpdateEmpSign(EmployeeSignDto employeeSignDto) {
        String signData = employeeSignDto.getEmpSign();
        int empNo = employeeSignDto.getEmpNo();
        if(signData != null && !signData.isEmpty()) {
            try{
                String fileName = imageService.storeSignatureImage(signData, empNo);
                String storedUrl = "/upload/" + fileName;
                employeeSignDto.setEmpSign(storedUrl);
            } catch (IOException e) {
                throw new RuntimeException("서명 저장에 실패했습니다.", e);
            }
        }

        employeeMapper.insertOrUpdateEmpSign(employeeSignDto);
    }

    /**
     *
     * 프로필 이미지가 존재하면 업데이트
     * 프로필 이미지가 존재하지 않으면 삽입
     * @param empNo
     * @param file
     * @throws IOException
     */
    @Transactional
    public void insertOrUpdateEmpProfile(Integer empNo,
                                         MultipartFile file) throws IOException {
        if(file != null && !file.isEmpty()) {
            String storedProfileImage = imageService.storeProfileImage(file);
            String storedUrl = "/profiles/" + storedProfileImage;
            EmployeeProfileDto employeeProfileDto = EmployeeProfileDto.builder()
                    .empNo(empNo)
                    .empProfile(storedUrl)
                    .modifyDt(Date.from(Instant.now()))
                    .build();
            employeeMapper.insertOrUpdateEmpProfile(employeeProfileDto);
        }
    }

    // 전화번호 업데이트
    @Transactional
    public void changeEmpPhone(String empPhone, Integer empNo, Integer modifier) {
        if (empNo == null) {
            throw new IllegalArgumentException("해당하는 직원이 존재하지 않습니다.");
        }
        Date date = new Date();
        employeeMapper.updateEmpPhone(empPhone, date, modifier, empNo);
    }

    // 이름 업데이트
    @Transactional
    public void changeEmpName(String empName, Integer empNo, Integer modifier) {
        if (empNo == null) {
            throw new IllegalArgumentException("해당하는 직원이 존재하지 않습니다.");
        }
        Date date = new Date();
        employeeMapper.updateEmpName(empName, date, modifier, empNo);
    }

    // 직책 변경
    @Transactional
    public void changePosNo(Integer posNo, Integer empNo, Integer modifier) {
        if (empNo == null) {
            throw new IllegalArgumentException("해당하는 직원이 존재하지 않습니다.");
        }
        Date date = new Date();
        employeeMapper.updatePosNo(Position.of(posNo), date, modifier, empNo);
    }

    // 재직 상태 변경
    @Transactional
    public void changeEmpStat(Integer empStat, Integer empNo, Integer modifier) {
        if(empNo == null) {
            throw new IllegalArgumentException("해당하는 직원이 존재하지 않습니다.");
        }
        employeeMapper.updateEmpStat(EmpStatus.fromValue(empStat), modifier, empNo);
    }


}
