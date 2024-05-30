package com.mcp.crispy.board.mapper;

import com.mcp.crispy.board.dto.BoardDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BoardMapper {

    List<BoardDto> getFreeBoardList();

    List<BoardDto> getNotiBoardList();
}