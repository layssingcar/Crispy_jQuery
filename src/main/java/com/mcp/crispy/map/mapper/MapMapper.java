package com.mcp.crispy.map.mapper;

import com.mcp.crispy.map.dto.MapDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MapMapper {

    List<MapDto> getMapList();

}
