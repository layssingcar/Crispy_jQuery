package com.mcp.crispy.freeboard.mapper;

import com.mcp.crispy.freeboard.dto.FreeBoardDto;
import com.mcp.crispy.freeboard.dto.FreeBoardFileDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;


@Mapper
public interface FreeBoardMapper {
    // 자유게시판 LIST
    List<FreeBoardDto> getFreeBoardList(FreeBoardDto freeBoardDto, RowBounds rowBounds);
    // 게시판 생성
    int insertFreeBoard(FreeBoardDto freeBoardDto);
    // 파일 추가
    int insertFreeBoardFile(FreeBoardFileDto BoardFile);
    // 게시판 수
    int getFreeBoardCount();
    // 게시판 번호로 정보 가져오기
    FreeBoardDto getFreeBoardByNo(int BoardNo);
    // 게시판내에 파일 리스트 가져오기
    List<FreeBoardFileDto> getFreeBoardFileList(int BoardNo);
    // 파일번호로 파일 정보 가져오기
    FreeBoardFileDto getFreeBoardFileByNo(int BoardFileNo);

    // 게시판 수정
    void updateFreeBoard(FreeBoardDto Board);
    // 게시판 삭제
    void deleteFreeBoard(@Param("boardNo")int BoardNo, @Param("modifier") int modifier);
    // 파일 삭제
    void deleteFreeBoardFile(int BoardFileNo);


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