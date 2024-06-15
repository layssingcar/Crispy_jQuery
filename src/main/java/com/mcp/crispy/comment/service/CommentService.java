package com.mcp.crispy.comment.service;

import com.mcp.crispy.comment.dto.CommentDto;
import com.mcp.crispy.comment.mapper.CommentMapper;
import com.vane.badwordfiltering.BadWordFiltering;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.mcp.crispy.comment.service.BadWordFilteringHelper.getBadWordFiltering;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentMapper commentMapper;

    // 댓글 작성
    @Transactional
    public CommentDto insertComment(CommentDto commentDto, int creator) {
        log.info("Insert comment into database: {}", creator);

        CommentDto comment = CommentDto.builder()
                .cmtContent(commentDto.getCmtContent())
                .creator(creator)
                .boardNo(commentDto.getBoardNo())
                .parentCmtNo(commentDto.getParentCmtNo())
                .build();
        log.info("Insert comment {}", comment);
        commentMapper.insertComment(comment);
        return comment;
    }

    // 게시글에 달린 댓글 조회
    @Transactional(readOnly = true)
    public List<CommentDto> getComments(int boardNo) {
        BadWordFiltering badWordFiltering1 = getBadWordFiltering();
        List<CommentDto> commentDtos = commentMapper.selectCommentByNo(boardNo);
        commentDtos.forEach(comment -> {
            String change = badWordFiltering1.change(comment.getCmtContent());
            comment.setCmtContent(change);
        });
        return commentMapper.selectCommentByNo(boardNo);
    }

    // 댓글 삭제
    public void deleteCommentByNo(int cmtNo, int modifier) {
        commentMapper.deleteCommentByNo(cmtNo, modifier);
    }

    // 댓글 수정
    public void updateComment(CommentDto commentDto, int modifier) {
        CommentDto existingComment = commentMapper.getComment(commentDto.getCmtNo());
        log.info("Update comment {}, modifier: {}", existingComment, modifier);
        // 댓글 작성자 검증
        if (existingComment == null || existingComment.getCreator() != modifier) {;
            throw new IllegalArgumentException("댓글 작성자만 댓글을 수정할 수 있습니다.");
        }

        CommentDto comment = CommentDto.builder()
                .cmtNo(commentDto.getCmtNo())
                .cmtContent(commentDto.getCmtContent())
                .modifier(modifier)
                .build();
        commentMapper.updateComment(comment);
    }

    public int getCountComment(int boardNo) {
        return commentMapper.getCountComment(boardNo);
    }
}
