package com.mcp.crispy.board.mapper;

import com.mcp.crispy.board.dto.BoardDto;
import com.mcp.crispy.board.dto.BoardFileDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;


@Mapper
public interface BoardMapper {
    // 자유게시판 LIST
    List<BoardDto> getFreeBoardList(BoardDto boardDto, RowBounds rowBounds);
    // 게시판 생성
    int insertBoard(BoardDto boardDto);
    // 파일 추가
    int insertBoardFile(BoardFileDto BoardFile);
    // 게시판 수
    int getBoardCount();
    // 게시판 번호로 정보 가져오기
    BoardDto getBoardByNo(int BoardNo);
    // 게시판내에 파일 리스트 가져오기
    List<BoardFileDto> getBoardFileList(int BoardNo);
    // 파일번호로 파일 정보 가져오기
    BoardFileDto getBoardFileByNo(int BoardFileNo);

    // 게시판 수정
    void updateBoard(BoardDto Board);
    // 게시판 삭제
    void deleteBoard(@Param("boardNo")int BoardNo, @Param("modifier") int modifier);
    // 파일 삭제
    void deleteBoardFile(int BoardFileNo);


    // 좋아요 확인
    int isLiked(@Param("boardNo") int BoardNo, @Param("empNo") int empNo);
    // 좋아요 추가
    void addLike(@Param("boardNo") int BoardNo, @Param("empNo") int empNo);
    // 좋아요 삭제
    void removeLike(@Param("boardNo") int BoardNo, @Param("empNo") int empNo);
    // 좋아요 수 증가
    void updateLikeCount(@Param("boardLikeCount") int boardLikeCount, @Param("boardNo") int boardNo);

    // 조회수
    void increaseBoardHit(int boardNo);
}