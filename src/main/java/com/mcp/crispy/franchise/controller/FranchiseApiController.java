package com.mcp.crispy.franchise.controller;

import com.mcp.crispy.common.utils.FranchiseUtil;
import com.mcp.crispy.franchise.dto.FranchiseRegistrationRequest;
import com.mcp.crispy.franchise.dto.FrnUpdateDto;
import com.mcp.crispy.franchise.service.FranchiseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/franchise")
public class FranchiseApiController {

    private final FranchiseService franchiseService;
    private final FranchiseUtil franchiseUtil;

    /**
     * 가맹점, 점주 등록
     * 배영욱 (24. 05. 20)
     * @param request 가맹점, 점주 등록 요청 DTO
     * @return ResponseEntity
     */
    @PostMapping("/register/v1")
    public ResponseEntity<Map<String, String>> registerFranchise(@Valid @RequestBody FranchiseRegistrationRequest request) {

        franchiseService.registerFranchiseAndOwner(request.getFranchiseRegisterDto(), request.getOwnerRegisterDto());
        return ResponseEntity.ok(Map.of("message", "가맹점 등록이 성공적으로 되었습니다."));
    }

    /**
     * 가맹점 사진 변경
     * 배영욱 (24. 06. 02)
     * @param frnNo 가맹점 번호
     * @param file 가맹점 사진 파일
     * @return ResponseEntity
     */
//    @IsOwner
    @PostMapping(value = "/frnImg/v1")
    public ResponseEntity<?> changeFrnImg(@RequestParam("frnNo") Integer frnNo,
                                          @RequestPart(value = "file", required = false) MultipartFile file) {
        Integer modifier = franchiseUtil.getModifier();
        log.info("file: {}", file);
        try {
            franchiseService.updateFrnImg(frnNo, file, modifier);
            return ResponseEntity.ok(Map.of("message", "가맹점 사진이 성공적으로 변경 되었습니다."));

        } catch (IOException ex) {
            return ResponseEntity.internalServerError().body(Map.of("error", "가맹점 사진이 변경에 실패했습니다."));
        }
    }

    /**
     * 가맹점 전화번호 변경
     * 배영욱 (24. 06. 02)
     * @param frnUpdateDto 가맹점 업데이트 DTO
     * @return ResponseEntity
     */
//    @IsOwner
    @PutMapping("/frnTel/v1")
    public ResponseEntity<?> changeFrnTel(@RequestBody FrnUpdateDto frnUpdateDto) {
        Integer modifier = franchiseUtil.getModifier();
        log.info("frnNo: {}", frnUpdateDto.getFrnNo());
        franchiseService.changeFrnTel(frnUpdateDto.getFrnTel(), frnUpdateDto.getFrnNo(), modifier);
        return ResponseEntity.ok(Map.of("message", "가맹점 전화번호가 변경되었습니다."));
    }

    /**
     * 가맹점 주소 변경
     * 배영욱 (24. 06. 02)
     * @param frnUpdateDto 가맹점 업데이트 DTO
     * @return ResponseEntity
     */
//    @IsOwner
    @PostMapping("/frnAddress/v1")
    public ResponseEntity<Map<String, String>> changeFrnAddress(@RequestBody FrnUpdateDto frnUpdateDto) {
        Integer modifier = franchiseUtil.getModifier();
        franchiseService.updateFrnAddress(frnUpdateDto, modifier);
        return ResponseEntity.ok(Map.of("message", "주소가 변경되었습니다."));
    }

    /**
     * 대표자 이름 변경
     * 배영욱 (24. 06. 02)
     * @param frnUpdateDto 가맹점 업데이트 DTO
     * @return ResponseEntity 성공 메시지
     */
//    @IsOwner
    @PutMapping("/frnOwner/v1")
    public ResponseEntity<?> changeFrnOwner(@RequestBody FrnUpdateDto frnUpdateDto) {
        Integer modifier = franchiseUtil.getModifier();
        franchiseService.changeFrnOwner(frnUpdateDto.getFrnOwner(), frnUpdateDto.getEmpNo(),modifier, frnUpdateDto.getFrnNo());
        log.info("getEmpNo: {}", frnUpdateDto.getEmpNo());
        log.info("getEmpNo: {}", modifier);
        return ResponseEntity.ok(Map.of("message", "대표자명이 성공적으로 변경되었습니다."));
    }

    /**
     * 운영 시간 변경
     * 배영욱 (24. 06. 02)
     * @param frnUpdateDto 가맹점 업데이트 DTO
     * @return ResponseEntity 성공 메시지
     */
    @PutMapping("/operatingTime/v1")
    public ResponseEntity<?> changeOperatingTime(@RequestBody FrnUpdateDto frnUpdateDto) {
        Integer modifier = franchiseUtil.getModifier();
        franchiseService.changeOperatingTime(frnUpdateDto, modifier);
        return ResponseEntity.ok(Map.of("message", "운영시간이 성공적으로 변경되었습니다."));
    }
}
