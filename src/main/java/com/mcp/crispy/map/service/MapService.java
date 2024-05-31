package com.mcp.crispy.map.service;

import com.mcp.crispy.map.mapper.MapMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MapService {

    private final MapMapper mapMapper;
}
