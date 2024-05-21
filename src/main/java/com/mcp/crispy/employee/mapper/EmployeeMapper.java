package com.mcp.crispy.employee.mapper;

import com.mcp.crispy.employee.dto.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;


@Mapper
public interface EmployeeMapper {

	void insertEmployee(EmployeeRegisterDto employee);

	Optional<EmployeeDto> findByUsername(String username);

	Optional<EmployeeDto> findByEmployeeDetailsByEmpNo(Integer empNo);

	List<EmployeeDto> findEmployeeByFranchise(int frnNo);

	// 이메일로 아이디(username) 찾기
	Optional<FindEmployeeDto> findByEmpEmail(@Param("empEmail")String email, @Param("empName")String empName);

	//비밀번호 변경
	void updateEmpPw(@Param("empId")String username, @Param("empPw")String password);

	// 닉네임, 이메일에 해당하는 직원의 정보 찾기
	Optional<FindEmployeeDto> findByEmpNameAndEmpEmail(@Param("empName") String empName, @Param("empEmail") String empEmail);

	// 닉네임, 이메일, 아이디에 해당하는 직원의 정보 찾기
	Optional<FindEmployeeDto> findByEmpNameAndEmpEmailAndEmpId(@Param("empName") String empName, @Param("empEmail") String empEmail, @Param("empId") String empId);

	// empNo에 맞는 직원 찾기
	Optional<EmployeeDto> findEmployeeByEmpNo(Integer empNo);

	// 주소 데이터가 존재하면 Update 존재하지 않으면 Insert
	void insertOrUpdateAddress(EmpAddressDto empAddressDto);

	// 서명 데이터가 존재하면 Update 존재하지 않으면 Insert
	void insertOrUpdateEmpSign(EmployeeSignDto employeeSignDto);

	// 프로필 이미지 데이터가 존재하면 Update 존재하지 않으면 Insert
	void insertOrUpdateEmpProfile(EmployeeProfileDto employeeProfileDto);

	// 전화번호 변경
	void updateEmpPhone(@Param("empPhone") String empPhone, @Param("modifyDt") Date modifyDt,
						@Param("modifier") Integer modifier, @Param("empNo") Integer empNo);
	// 이름 변경
	void updateEmpName(@Param("empName") String empName, @Param("modifyDt") Date modifyDt,
					   @Param("modifier") Integer modifier, @Param("empNo") Integer empNo);
	// 직책 변경
	void updatePosNo(@Param("posNo") Position posNo, @Param("modifyDt") Date modifyDt,
					 @Param("modifier") Integer modifier, @Param("empNo") Integer empNo);

	// 상태 변경
	void updateEmpStat(@Param("empStat") EmpStatus empStat, @Param("modifier") Integer modifier, @Param("empNo") Integer empNo);
}
