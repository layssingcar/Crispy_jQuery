package com.mcp.crispy.employee.mapper;

import com.mcp.crispy.employee.dto.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;


@Mapper
public interface EmployeeMapper {

	void insertEmployee(EmployeeRegisterDto employee);

	Optional<EmployeeDto> findByUsername(String username);

	List<EmployeeDto> findAllExceptCurrentUser(@Param("currentEmpNo") Integer empNo);

	Optional<EmployeeDto> findByEmployeeDetailsByEmpNo(Integer empNo);

	List<EmployeeDto> findEmployeeByFranchise(int frnNo);

	// 이메일로 아이디(username) 찾기
	Optional<FindEmployeeDto> findByEmpEmail(@Param("empEmail")String email, @Param("empName")String empName);

	//비밀번호 변경
	void updateEmpPw(@Param("empId") String empId, @Param("empPw")String empPw, @Param("modifier") Integer modifier);

	// 닉네임, 이메일에 해당하는 직원의 정보 찾기
	Optional<FindEmployeeDto> findByEmpNameAndEmpEmail(@Param("empName") String empName, @Param("empEmail") String empEmail);

	// 닉네임, 이메일, 아이디에 해당하는 직원의 정보 찾기
	Optional<FindEmployeeDto> findByEmpNameAndEmpEmailAndEmpId(@Param("empName") String empName, @Param("empEmail") String empEmail, @Param("empId") String empId);

	// empNo에 맞는 직원 찾기
	Optional<EmployeeDto> findEmployeeByEmpNo(Integer empNo);

	// 번호에 맞는 직원 호출
	int countByEmpNo(Integer empNo);

	// 주소 수정
	void updateAddress(@Param("updateDto") EmployeeUpdateDto employeeUpdateDto, @Param("modifier") Integer modifier);

	// 전자 서명 수정
	void updateEmpSign(EmployeeUpdateDto employeeUpdateDto);

	// 프로필 수정
	void updateEmpProfile(@Param("empProfile") String empProfile, @Param("empNo") Integer empNo,
						  @Param("modifier") Integer modifier);

	// 전화번호 수정
	void updateEmpPhone(@Param("empPhone") String empPhone, @Param("empNo") Integer empNo,
						@Param("modifier") Integer modifier);
	// 이름 수정
	void updateEmpName(@Param("empName") String empName, @Param("empNo") Integer empNo,
					   @Param("modifier") Integer modifier);
	// 직책 수정
	void updatePosNo(@Param("posNo") Position posNo, @Param("empNo") Integer empNo,
					 @Param("modifier") Integer modifier);

	// 상태 수정
	void updateEmpStat(@Param("empStat") EmpStatus empStat, @Param("empNo") Integer empNo,
					   @Param("modifier") Integer modifier);

	// 직원 검색
	List<EmployeeDto> searchEmployees(@Param("empName") String empName, @Param("currentEmpNo") Integer empNo);

	// 직원 초대
	List<EmployeeDto> inviteEmployees(@Param("chatRoomNo") Integer chatRoomNo);

	// 폼 수정 메소드
	void updateFormEmployee(@Param("updateDto")EmployeeUpdateDto employeeUpdateDto, @Param("modifier")Integer modifier);
}
