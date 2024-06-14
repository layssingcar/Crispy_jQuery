package com.mcp.crispy.annual.mapper;

import com.mcp.crispy.annual.dto.AnnualDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AnnualMapper {
	int insertAnnual(AnnualDto annualDto);
	List<AnnualDto> getAnnList(int empNo);
	AnnualDto getAnnById(String id);
	int modifyAnnual(AnnualDto annualDto);
	int deleteAnnual(AnnualDto annualDto);
	int completeDeleteAnn(AnnualDto annualDto);
	int revertAnnual(AnnualDto annualDto);
	
	int getCountAnn();
}
