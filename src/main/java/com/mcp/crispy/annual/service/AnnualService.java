package com.mcp.crispy.annual.service;

import com.mcp.crispy.annual.dto.AnnualDto;
import com.mcp.crispy.annual.mapper.AnnualMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnnualService {

	private final AnnualMapper annualMapper;
	
	@Transactional
	public int insertAnnual(AnnualDto annualDto){
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
	public int completeDeleteAnnual(int scheduleId){
		return annualMapper.completeDeleteAnnual(scheduleId);
	}
}
