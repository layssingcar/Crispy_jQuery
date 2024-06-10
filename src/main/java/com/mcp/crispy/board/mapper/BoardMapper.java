package com.mcp.crispy.board.mapper;

import java.util.List;
import java.util.Map;

import com.mcp.crispy.board.dto.BoardDto;
import com.mcp.crispy.board.dto.BoardFileDto;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface BoardMapper {
    // 자유게시판 LIST
    List<BoardDto> getFreeBoardList();
    int insertBoard(BoardDto board);
    int insertBoardFile(BoardFileDto BoardFile);
    int getBoardCount();
    List<BoardDto> getBoardList(Map<String, Object> map);
    BoardDto getBoardByNo(int BoardNo);
    List<BoardFileDto> getBoardFileList(int BoardNo);
    BoardFileDto getBoardFileByNo(int BoardFileNo);
    //    int updateDownloadCount(int BoardFileNo);
    int updateBoard(BoardDto Board);
    int deleteBoardFile(int BoardFileNo);
    int deleteBoard(int BoardNo);
}