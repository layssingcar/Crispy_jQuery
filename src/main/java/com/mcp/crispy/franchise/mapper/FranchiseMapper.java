package com.mcp.crispy.franchise.mapper;

import com.mcp.crispy.franchise.dto.FranchiseDto;
import com.mcp.crispy.franchise.dto.FranchiseRegisterDto;
import com.mcp.crispy.franchise.dto.FrnUpdateDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FranchiseMapper {

    // 가맹점 등록
    void insertFranchise(FranchiseRegisterDto franchiseRegisterDto);

    // 번호에 맞는 가맹점 호출
    int countByFrnNo(Integer frnNo);

    // 가맹점 정보 empId로 찾기
    FranchiseDto findByEmpId(String empId);

    // 가맹점 번호 수정
    void updateFrnTel(@Param("frnTel") String frnTel, @Param("frnNo") Integer frnNo, @Param("modifier") Integer modifier);

    // 가맹점 사진 수정
    void updateFrnImg(@Param("frnImg") String frnImg, @Param("frnNo") Integer frnNo,
                      @Param("modifier") Integer modifier);

    // 주소 수정
    void updateFrnAddress(@Param("updateDto") FrnUpdateDto frnUpdateDto, @Param("modifier") Integer modifier);

    // 대표자 이름 수정
    void updateFrnOwner(@Param("frnOwner") String empName, @Param("frnNo") Integer frnNo, @Param("modifier") Integer modifier);

    // 운영시간 수정
    void updateOperatingTime(@Param("frnStartTime") String frnStartTime, @Param("frnEndTime") String frnEndTime,
                             @Param("modifier") Integer modifier, @Param("frnNo") Integer frnNo);

    List<FranchiseDto> getFranchiseList();

    // 직원번호로 가맹점 정보 가져오기 ( 대표 이름 수정 용 )
    FranchiseDto getFrnByEmpNo(Integer empNo);
}
