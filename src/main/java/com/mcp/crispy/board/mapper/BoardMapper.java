package com.mcp.crispy.board.mapper;

import com.mcp.crispy.board.dto.BoardDto;
import com.mcp.crispy.board.dto.BoardFileDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


@Mapper
public interface BoardMapper {
    // 자유게시판 LIST
    List<BoardDto> getFreeBoardList();
    int insertBoard(BoardDto boardDto);
    int insertBoardFile(BoardFileDto BoardFile);
    int getBoardCount();
    List<BoardDto> getBoardList(Map<String, Object> map);
    BoardDto getBoardByNo(int BoardNo);
    List<BoardFileDto> getBoardFileList(int BoardNo);
    BoardFileDto getBoardFileByNo(int BoardFileNo);
    //    int updateDownloadCount(int BoardFileNo);
    int updateBoard(BoardDto Board);
    int deleteBoardFile(int BoardFileNo);
    int deleteBoard (@Param("boardNo")int BoardNo, @Param("empNo") int empNo);


    // 좋아요
    int isLiked(@Param("boardNo") int BoardNo, @Param("empNo") int empNo);
    void addLike(@Param("boardNo") int BoardNo, @Param("empNo") int empNo);
    void removeLike(@Param("boardNo") int BoardNo, @Param("empNo") int empNo);
    void updateLikeCount(@Param("boardLikeCount") int boardLikeCount, @Param("boardNo") int boardNo);
}