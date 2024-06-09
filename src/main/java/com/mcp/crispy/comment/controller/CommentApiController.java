package com.mcp.crispy.comment.controller;


import com.mcp.crispy.auth.domain.EmployeePrincipal;
import com.mcp.crispy.comment.dto.CommentDto;
import com.mcp.crispy.comment.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments")
public class CommentApiController {
    private final CommentService commentService;


    // 댓글 등록
    @PostMapping("/v1")
    public ResponseEntity<?> createComment(@Valid  @RequestBody CommentDto commentDto,
                                           Authentication authentication) {
        EmployeePrincipal employee = (EmployeePrincipal) authentication.getPrincipal();
        CommentDto comment = commentService.insertComment(commentDto, employee.getEmpNo());
        log.info("createComment: {}", comment.toString());
        return ResponseEntity.ok(Map.of("message", "댓글이 작성되었습니다."));
    }

    // 댓글 조회
    @GetMapping("/v1/{boardNo}")
    public ResponseEntity<?> getCommentByBoardNo(@PathVariable("boardNo") int boardNo) {
        List<CommentDto> comments = commentService.getComments(boardNo);
        return ResponseEntity.ok(comments);
    }

    // 댓글 삭제
    @DeleteMapping("/{cmtNo}/v1")
    public ResponseEntity<?> deleteComment(@PathVariable Integer cmtNo, Authentication authentication) {
        EmployeePrincipal employee = (EmployeePrincipal) authentication.getPrincipal();
        commentService.deleteCommentByNo(cmtNo, employee.getEmpNo());
        return ResponseEntity.ok(Map.of("message", "댓글이 삭제되었습니다."));
    }

    // 댓글 수정
    @PutMapping("/v1")
    public ResponseEntity<?> updateComment(@Valid @RequestBody CommentDto commentDto,
                                           Authentication authentication) {
        EmployeePrincipal employee = (EmployeePrincipal) authentication.getPrincipal();
        commentService.updateComment(commentDto, employee.getEmpNo());
        return ResponseEntity.ok(Map.of("message", "댓글이 수정되었습니다."));
    }
}
