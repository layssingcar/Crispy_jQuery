package com.mcp.crispy.board.service;

import com.mcp.crispy.board.dto.BoardDto;
import com.mcp.crispy.board.mapper.BoardMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardMapper boardMapper;

    public List<BoardDto> getFreeBoardList() {
        return  boardMapper.getFreeBoardList();
    }

    public List<BoardDto> getNotiBoardList() {
        return  boardMapper.getNotiBoardList();
    }
}