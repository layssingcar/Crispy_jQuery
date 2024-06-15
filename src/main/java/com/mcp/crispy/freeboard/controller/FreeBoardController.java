package com.mcp.crispy.freeboard.controller;


import com.mcp.crispy.auth.domain.EmployeePrincipal;
import com.mcp.crispy.comment.dto.CommentDto;
import com.mcp.crispy.comment.service.CommentService;
import com.mcp.crispy.common.page.PageResponse;
import com.mcp.crispy.freeboard.dto.FreeBoardDto;
import com.mcp.crispy.freeboard.service.FreeBoardService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/crispy")
public class FreeBoardController {

    private final FreeBoardService freeBoardService;
    private final CommentService commentService;

    /**
     * 자유게시판 LIST
     * 배영욱 (24. 06. 08)
     * @param model
     * @return forward (freeboard/free-board-list.html)
     */
    @GetMapping("/board-list/free")
    public String boardList(Model model, FreeBoardDto freeBoardDto) {
        PageResponse<FreeBoardDto> freeList = freeBoardService.getFree2BoardList(freeBoardDto,10);
        freeList.getItems().forEach(board -> {
            int countComment = commentService.getCountComment(board.getBoardNo());
            board.setCommentCount(countComment);
            log.info("countCommentCount: {}", countComment);
        });
        log.info("freeList: {}", freeList.getItems());
        model.addAttribute("freeList", freeList);
        return "freeboard/free-board-list";
    }

    @GetMapping("freeBoardItems")
    public String boardItems(Model model, FreeBoardDto freeBoardDto) {
        PageResponse<FreeBoardDto> freeList = freeBoardService.getFree2BoardList(freeBoardDto, 10);
        freeList.getItems().forEach(board -> {
            int countComment = commentService.getCountComment(board.getBoardNo());
            board.setCommentCount(countComment);
        });
        model.addAttribute("freeList", freeList);

        return "freeboard/free-board-list :: board-list-container";
    }

    @GetMapping("/freeBoard/save")
    public String insertBoard(Model model) {
        model.addAttribute("board", new FreeBoardDto());
        return "freeboard/free-board-add";
    }

    @GetMapping("/freeBoardDetail")
    public String detail(@RequestParam(value = "boardNo", required = false, defaultValue = "0") int boardNo,
                         Model model, Authentication authentication,
                         HttpServletRequest request, HttpServletResponse response) {
        log.info("boardNo: " + boardNo);
        EmployeePrincipal employee = (EmployeePrincipal) authentication.getPrincipal();
        freeBoardService.increaseFreeBoardHit(boardNo,request, response);
        FreeBoardDto freeBoardDto = freeBoardService.loadFreeBoardByNo(boardNo, employee.getEmpNo());
        log.info("freeBoardDto: {}", freeBoardDto.isHasAttachment());
        model.addAttribute("board", freeBoardDto);
        log.info("boardDto: {}", freeBoardDto.getBoardHit());
        model.addAttribute("files", freeBoardDto.getFiles());

        List<CommentDto> comments = commentService.getComments(boardNo);
        model.addAttribute("comments", comments);
        model.addAttribute("currentEmpNo", employee.getEmpNo()); // 현재 로그인한 사용자
        log.info("comments: " + comments);
        log.info("currentEmpNo: {}", employee.getEmpNo());
        log.info(freeBoardDto.toString());
        return "freeboard/free-board-detail";
    }

    @GetMapping("/freeBoardModify/{boardNo}")
    public String modify(@PathVariable int boardNo,
                         Model model, Authentication authentication) {
        log.info("boardNo: " + boardNo);
        EmployeePrincipal employee = (EmployeePrincipal) authentication.getPrincipal();
        FreeBoardDto freeBoardDto = freeBoardService.loadFreeBoardByNo(boardNo, employee.getEmpNo());
        model.addAttribute("files", freeBoardDto.getFiles());
        model.addAttribute("board", freeBoardDto);
        return "freeboard/free-board-modify";
    }
}
