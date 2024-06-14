package com.mcp.crispy.annual.mapper;

import com.mcp.crispy.annual.dto.AnnualDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AnnualMapper {
	int insertAnnual(AnnualDto scheduleDto);
	List<AnnualDto> getAnnList(int empNo);
	AnnualDto getAnnById(String id);
	int modifyAnnual(AnnualDto scheduleDto);
	int deleteAnnual(AnnualDto scheduleDto);
	int completeDeleteAnn(AnnualDto annualDto);
	
	int getCountAnn();
}
