package com.mcp.crispy.annual.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.mcp.crispy.annual.dto.AnnualDto;

@Mapper
public interface AnnualMapper {
	int insertAnnual(AnnualDto scheduleDto);
	List<AnnualDto> getAnnList();
}
