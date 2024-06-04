package com.mcp.crispy.map.service;

import com.mcp.crispy.map.dto.MapDto;
import com.mcp.crispy.map.mapper.MapMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MapService {

    private final MapMapper mapMapper;

    // 가맹점 목록 및 정보조회
    public List<MapDto> getMapList() {
        return mapMapper.getMapList();
    }
}


