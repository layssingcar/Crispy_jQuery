package com.mcp.crispy.franchise.controller;

import com.mcp.crispy.franchise.dto.FranchiseDto;
import com.mcp.crispy.franchise.dto.FranchiseRegistrationRequest;
import com.mcp.crispy.franchise.dto.FranchiseTelDto;
import com.mcp.crispy.franchise.dto.FrnAddressDto;
import com.mcp.crispy.franchise.service.FranchiseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/franchise")
public class FranchiseApiController {

    private final FranchiseService franchiseService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerFranchise(@RequestBody FranchiseRegistrationRequest request) {

        franchiseService.registerFranchiseAndOwner(request.getFranchiseDto(), request.getOwnerRegisterDto());
        return ResponseEntity.ok(Map.of("message", "가맹점 등록이 성공적으로 되었습니다."));
    }

//    @IsOwner
    @PostMapping(value = "/updateFrnImg", consumes = "multipart/form-data")
    public ResponseEntity<?> insertOrUpdateEmpProfile(@RequestParam("frnNo") Integer frnNo,
                                                      @RequestPart(value = "file", required = false) MultipartFile file) {
        log.info("file: {}", file);
        try {
            franchiseService.insertOrUpdateFrnImg(frnNo, file);
            return ResponseEntity.ok(Map.of("message", "주소가 성공적으로 추가/업데이트 되었습니다."));

        } catch (IOException ex) {
            return ResponseEntity.internalServerError().body(Map.of("error", "프로필 이미지 업데이트에 실패했습니다."));
        }
    }

//    @IsOwner
    @PutMapping("/updateFrnTel")
    public ResponseEntity<?> changeFrnTel(@RequestBody FranchiseTelDto franchiseTelDto,
                                          Principal principal) {
        FranchiseDto franchise = franchiseService.getFranchise(principal.getName());
        log.info("frnNo: {}", franchise.getFrnNo());
        franchiseService.changeFrnTel(franchiseTelDto.getFrnTel(), franchiseTelDto.getFrnNo(), franchiseTelDto.getEmpNo());
        return ResponseEntity.ok(Map.of("message", "가맹점 전화번호가 변경되었습니다."));
    }

//    @IsOwner
    @PostMapping("/updateFrnAddress")
    public ResponseEntity<Map<String, String>> insertOrUpdateAddress(@RequestBody FrnAddressDto frnAddressDto,
                                                                     Principal principal) {
        FranchiseDto franchise = franchiseService.getFranchise(principal.getName());
        log.info("EmpNo: {}", franchise.getEmpNo());
        franchiseService.insertOrUpdateFrnAddress(frnAddressDto, franchise.getEmpNo());
        return ResponseEntity.ok(Map.of("message", "주소가 성공적으로 추가/업데이트 되었습니다."));
    }

//    @IsOwner
//    @PostMapping("/updateFrnOnwer")
//    public ResponseEntity<?>


}
