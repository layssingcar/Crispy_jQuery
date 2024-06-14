package com.mcp.crispy.annual.service;

import com.mcp.crispy.annual.dto.AnnualDto;
import com.mcp.crispy.annual.mapper.AnnualMapper;
import com.mcp.crispy.employee.dto.EmployeeDto;
import com.mcp.crispy.employee.service.EmployeeService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnnualService {

	private final AnnualMapper annualMapper;
	private final EmployeeService employeeService;
	
	@Transactional
	public int insertAnnual(AnnualDto annualDto){
		EmployeeDto emp = employeeService.getEmployeeDetailsByEmpNo(annualDto.getEmpNo());
		
		if(annualDto.getAnnCtNo() == 0) {
			emp.setEmpAnnual(emp.getEmpAnnual() - 1);
		}
		else if(annualDto.getAnnCtNo() == 1) {
			emp.setEmpAnnual(emp.getEmpAnnual() - 0.5);
		}
		else if(annualDto.getAnnCtNo() == 2){
			emp.setEmpAnnual(emp.getEmpAnnual() - 0.25);
		}
		employeeService.updateAnnual(emp);
		
		return annualMapper.insertAnnual(annualDto);
	}
	
	@Transactional(readOnly = true)
	public List<AnnualDto> getAnnList(int empNo) {
		List<AnnualDto> annList = annualMapper.getAnnList(empNo); 
		return annList;
	}
	
	@Transactional(readOnly = true)
	public AnnualDto getAnnById(String id) {
		return annualMapper.getAnnById(id);
	}
	
	@Transactional
	public int modifyAnnual(AnnualDto annualDto){
		return annualMapper.modifyAnnual(annualDto);
	}
	@Transactional
	public int deleteAnnual(AnnualDto annualDto){
		return annualMapper.deleteAnnual(annualDto);
	}
	@Transactional
	public int completeDeleteAnn(AnnualDto annualDto){
		return annualMapper.completeDeleteAnn(annualDto);
	}
	public int revertAnnual(AnnualDto annualDto) {
		return annualMapper.revertAnnual(annualDto);
	}
	
	@Transactional(readOnly = true)
	public int getCountAnn() {
		return annualMapper.getCountAnn();
	}
}
