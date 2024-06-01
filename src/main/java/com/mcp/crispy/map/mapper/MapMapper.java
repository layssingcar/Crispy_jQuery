package com.mcp.crispy.map.mapper;

import com.mcp.crispy.map.dto.MapDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MapMapper {

    // 가맹점 목록 및 정보조회
    List<MapDto> getMapList();

}
