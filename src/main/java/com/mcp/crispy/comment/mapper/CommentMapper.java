package com.mcp.crispy.comment.mapper;

import com.mcp.crispy.comment.dto.CommentDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {

    // 댓글 작성
    void insertComment(CommentDto commentDto);

    // 게시판에 달린 댓글 조회
    List<CommentDto> selectCommentByNo(int boardNo);

    // 댓글 조회
    CommentDto getComment(int cmtNo);

    // 댓글 삭제
    void deleteCommentByNo(int cmtNo, int modifier);

    // 댓글 수정
    void updateComment(CommentDto commentDto);

    // 댜댓글 작성

    // 대댓글 조회

    // 대댓글 삭제

    // 대댓글 수정

    // 특정 게시글에 달린 댓글 개수
    int getCountComment(int boardNo);

}
