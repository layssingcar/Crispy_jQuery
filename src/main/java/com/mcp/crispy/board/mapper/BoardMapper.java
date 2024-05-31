package com.mcp.crispy.board.mapper;

import com.mcp.crispy.board.dto.BoardDto;
import com.mcp.crispy.board.dto.BoardFileDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BoardMapper {

    int insertBoardFile(BoardFileDto boardFile);

    List<BoardDto> getFreeBoardList();

    List<BoardDto> getNotiBoardList();

    int deleteBoard(int boardNo);

    BoardDto getBoardByNo(int boardNo);

    int insertBoard(BoardDto board);
}