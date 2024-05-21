package com.mcp.crispy.franchise.mapper;

import com.mcp.crispy.franchise.dto.FranchiseDto;
import com.mcp.crispy.franchise.dto.FranchiseImgDto;
import com.mcp.crispy.franchise.dto.FrnAddressDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalTime;

@Mapper
public interface FranchiseMapper {

    void insertFranchise(FranchiseDto franchiseDto);

    // 가맹점 정보 empId로 찾기
    FranchiseDto findByEmpId(String empId);

    // 이미지가 존재하면 Update 존재하지 않으면 Insert
    void insertOrUpdateFrnImg(FranchiseImgDto franchiseImgDto);

    // 주소 데이터가 존재하면 Update 존재하지 않으면 Insert
    void insertOrUpdateFrnAddress(@Param("frnAddressDto") FrnAddressDto frnAddressDto, @Param("modifier") Integer modifier);

    void updateFrnTel(@Param("frnTel") String frnTel, @Param("frnNo") Integer frnNo, @Param("modifier") Integer modifier);

    // 대표자 이름 변경
    void updateFrnOwner(@Param("frnOwner") String empName, @Param("modifier") Integer modifier, @Param("frnNo") Integer frnNo);

    // 운영시간 변경
    void updateOperatingTime(@Param("frnStartTime") LocalTime frnStartTime, @Param("frnEndTime") LocalTime frnEndTime,
                             @Param("modifier") Integer modifier, @Param("frnNo") Integer frnNo);
}
