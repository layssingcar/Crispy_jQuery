package com.mcp.crispy.employee.controller;

import com.mcp.crispy.common.utils.CookieUtil;
import com.mcp.crispy.email.service.EmailVerificationService;
import com.mcp.crispy.employee.dto.*;
import com.mcp.crispy.employee.service.EmployeeService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/employee")
public class EmployeeApiController {

    private final EmployeeService employeeService;
    private final EmailVerificationService emailVerificationService;
    private final Validator validator;


    /**
     * 아이디 찾기, 비밀번호 찾기 이메일 전송
     * 배영욱 (24. 05. 20)
     * @param findEmployeeDto 정보 찾기 DTO
     * @return ResponseEntity
     */
    @PostMapping("/verify/email/v1")
    public ResponseEntity<Map<String, String>> verifyEmployee(@RequestBody FindEmployeeDto findEmployeeDto) {
        boolean employeeExists = employeeService.checkEmployeeExists(findEmployeeDto.getEmpName(), findEmployeeDto.getEmpEmail(), findEmployeeDto.getEmpId());
        if(!employeeExists) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "일치하는 회원 정보가 없습니다."));
        }
        emailVerificationService.sendAndSaveVerificationCode(findEmployeeDto.getEmpEmail());
        return ResponseEntity.ok().body(Map.of("message", "인증 코드가 발송되었습니다."));
    }

    /**
     * 직원 상세 정보 조회
     * 배영욱 (24. 06. 02)
     * @param empNo 직원 번호
     * @return ResponseEntity
     */
//    @IsOwner
    @GetMapping("/{empNo}/v1")
    public ResponseEntity<EmployeeDto> getEmployeeDetails(@PathVariable Integer empNo) {
        EmployeeDto employee = employeeService.getEmployeeDetailsByEmpNo(empNo);
        if (employee != null) {
            log.info("employee: {} {} {}", employee.getEmpZip(), employee.getEmpStreet(), employee.getEmpDetail());
            return ResponseEntity.ok(employee);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 임시 비밀번호 전송
     * 배영욱 (24. 06. 02)
     * @param findEmployeeDto 정보 찾기 DTO
     * @return ResponseEntity
     */
    @PostMapping("/password/reset/v1")
    public ResponseEntity<Map<String, String>> getEmployeeResetPassword(@RequestBody FindEmployeeDto findEmployeeDto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        EmployeeDto employee = employeeService.getEmployeeName(auth.getName());
        employeeService.resetEmployeePassword(findEmployeeDto.getEmpEmail(), findEmployeeDto.getEmpName(), employee.getEmpNo());
        return ResponseEntity.ok(Map.of("message", "임시 비밀번호가 전송되었습니다."));
    }

    /**
     * 비밀번호 변경
     * 배영욱(24. 06. 02)
     * @param passwordChangeDto 비밀번호 변경 DTO
     * @param response HTTP 응답 객체
     * @return ResponseEntity
     */
    @PutMapping("/empPw/v1")
    public ResponseEntity<Map<String, String>> changeEmployeePassword(@Valid @RequestBody PasswordChangeDto passwordChangeDto,
                                                                      BindingResult bindingResult,
                                                                      HttpServletResponse response) {

        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                String field = error.getField();
                String message = error.getDefaultMessage();
                errors.put(field, message);
            }
            return ResponseEntity.badRequest().body(errors);
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        EmployeeDto employee = employeeService.getEmployeeName(auth.getName());
        employeeService.updateEmployeePassword(passwordChangeDto.getEmpId(), passwordChangeDto, employee.getEmpNo());
        CookieUtil.deleteCookie(response, "accessToken");
        CookieUtil.deleteCookie(response, "refreshToken");
        return ResponseEntity.ok(Map.of("message", "비밀번호가 성공적으로 업데이트 되었습니다. 다시 로그인 해주세요."));
    }

    /**
     * 주소 변경
     * 배영욱 (24. 06. 02)
     * @param empAddressUpdateDto 직원 주소 업데이트 DTO
     * @return ResponseEntity
     */
    @PostMapping("/address/v1")
    public ResponseEntity<Map<String, String>> updateAddress(@Valid @RequestBody EmpAddressUpdateDto empAddressUpdateDto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        EmployeeDto employee = employeeService.getEmployeeName(auth.getName());
        employeeService.updateAddress(empAddressUpdateDto, employee.getEmpNo());
        return ResponseEntity.ok(Map.of("message", "주소가 성공적으로 수정 되었습니다."));
    }

    /**
     * 전자 서명 변경
     * 배영욱 (24. 05. 20)
     * @param employeeUpdateDto 직원 정보 업데이트 DTO
     * @return ResponseEntity
     */
    @PutMapping("/empSign/v1")
    public ResponseEntity<Map<String, String>> updateEmpSign(@RequestBody EmployeeUpdateDto employeeUpdateDto) {
        employeeService.updateEmpSign(employeeUpdateDto);
        return ResponseEntity.ok(Map.of("message", "서명이 성공적으로 수정 되었습니다."));
    }

    /**
     * 프로필 이미지 변경
     * 배영욱 (24. 06. 02)
     * @param empNo 직원 번호
     * @param file 프로필 이미지 파일
     * @return ResponseEntity
     */
    @PostMapping(value = "/profileImg/v1")
    public ResponseEntity<Map<String, String>> updateEmpProfile(@RequestParam("empNo") Integer empNo,
                                                      @RequestPart(value = "file", required = false) MultipartFile file) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        EmployeeDto employee = employeeService.getEmployeeName(auth.getName());
        log.info("file: {}", file);
        try {
            employeeService.updateEmpProfile(empNo, file, employee.getEmpNo());
            log.info("updateEmpProfile: {}", employee.getEmpNo());
            return ResponseEntity.ok(Map.of("message", "프로필이 성공적으로 추가/업데이트 되었습니다."));

        } catch (IOException ex) {
            return ResponseEntity.internalServerError().body(Map.of("error", "프로필 이미지 업데이트에 실패했습니다."));
        }
    }

    /**
     * 휴대폰 번호 변경
     * 배영욱 (24. 06. 02)
     * @param empPhoneUpdateDto 직원 휴대폰 번호 업데이트 DTO
     * @return ResponseEntity
     */
    @PutMapping("/empPhone/v1")
    public ResponseEntity<Map<String, String>> changeEmpPhone(@Valid @RequestBody EmpPhoneUpdateDto empPhoneUpdateDto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        EmployeeDto employee = employeeService.getEmployeeName(auth.getName());
        employeeService.changeEmpPhone(empPhoneUpdateDto.getEmpPhone(), empPhoneUpdateDto.getEmpNo(), employee.getEmpNo());
        return ResponseEntity.ok(Map.of("message", "휴대폰번호가 변경되었습니다."));

    }

    /**
     * 이름 변경
     * 배영욱 (24. 06. 02)
     * @param employeeNameUpdateDto 직원 정보 업데이트 DTO
     * @return ResponseEntity
     */
    @PutMapping("/empName/v1")
    public ResponseEntity<Map<String, String>> changeEmpName(@Valid @RequestBody EmployeeNameUpdateDto employeeNameUpdateDto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        EmployeeDto employee = employeeService.getEmployeeName(auth.getName());
        employeeService.changeEmpName(employeeNameUpdateDto.getEmpName(), employeeNameUpdateDto.getEmpNo(), employee.getEmpNo());
        log.info("employee: {} {}", employeeNameUpdateDto.getEmpName(), employee.getEmpNo());
        return ResponseEntity.ok(Map.of("message", "이름이 변경되었습니다."));
    }

    /**
     * 직책 변경
     * 배영욱 (24. 06. 02)
     * @param employeeUpdateDto 직원 정보 업데이트 DTO
     * @return ResponseEntity
     */
    @PutMapping("/posNo/v1")
    public ResponseEntity<Map<String, String>> changePosNo(@RequestBody EmployeeUpdateDto employeeUpdateDto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        EmployeeDto employee = employeeService.getEmployeeName(auth.getName());
        log.info("PosNo: {}", employeeUpdateDto.getPosNo());
        employeeService.changePosNo(employeeUpdateDto.getPosNo().getCode(), employeeUpdateDto.getEmpNo(), employee.getEmpNo());
        return ResponseEntity.ok(Map.of("message", "직책이 변경되었습니다."));
    }

    /**
     * 이메일 변경
     * 배영욱 (24. 06. 04)
     * @param updateDto 직원 정보 업데이트 DTO
     * @return ResponseEntity
     */
    @PutMapping("/empEmail/v1")
    public ResponseEntity<Map<String, String>> changeEmail(@Valid @RequestBody EmployeeEmailUpdateDto updateDto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        EmployeeDto employee = employeeService.getEmployeeName(auth.getName());
        employeeService.changeEmail(updateDto.getEmpEmail(), updateDto.getEmpNo(), employee.getEmpNo());
        return ResponseEntity.ok(Map.of("message", "이메일이 변경되었습니다."));
    }

    /**
     * 재직 상태 변경
     * 배영욱 (24. 06. 02)
     * @param employeeUpdateDto 직원 정보 업데이트 DTO
     * @return ResponseEntity
     */
    @PutMapping("/empStat/v1")
    public ResponseEntity<Map<String, String>> changeEmpStat(@RequestBody EmployeeUpdateDto employeeUpdateDto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        EmployeeDto employee = employeeService.getEmployeeName(auth.getName());
        employeeService.changeEmpStat(employeeUpdateDto.getEmpStat().getValue(), employeeUpdateDto.getEmpNo(), employee.getEmpNo());
        return ResponseEntity.ok(Map.of("message", "재직 상태가 변경되었습니다."));
    }

    /**
     *
     * 정보 폼으로 수정
     * 배영욱 (24. 06. 02)
     * @param employeeUpdateDto 직원 정보 업데이트 DTO
     * @return ResponseEntity
     */
    @PutMapping("/form/v1")
    public ResponseEntity<Map<String, String>> changeForm(@Valid @RequestBody EmployeeUpdateDto employeeUpdateDto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        EmployeeDto employee = employeeService.getEmployeeName(auth.getName());
        employeeService.updateFormEmployee(employeeUpdateDto, employee.getEmpNo());
        return ResponseEntity.ok(Map.of("message", "정보가 수정되었습니다."));
    }
}
