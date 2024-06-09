package com.mcp.crispy.board.controller;


import com.mcp.crispy.auth.domain.EmployeePrincipal;
import com.mcp.crispy.board.dto.BoardDto;
import com.mcp.crispy.board.service.BoardService;
import com.mcp.crispy.comment.dto.CommentDto;
import com.mcp.crispy.comment.service.CommentService;
import com.mcp.crispy.common.page.PageResponse;
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
public class BoardController {

    private final BoardService boardService;
    private final CommentService commentService;

    /**
     * 오정은 - 자유게시판 LIST
     * @param model
     * @return forward (board/board-list.html)
     */
    @GetMapping("/board-list")
    public String boardList(Model model, BoardDto boardDto) {
        PageResponse<BoardDto> freeList = boardService.getFreeBoardList(boardDto,10);
        model.addAttribute("freeList", freeList);

        return "board/board-list";
    }

    @GetMapping("board-items")
    public String boardItems(Model model, BoardDto boardDto) {
        PageResponse<BoardDto> freeList = boardService.getFreeBoardList(boardDto, 10);
        model.addAttribute("freeList", freeList);

        return "board/board-list :: board-list-container";
    }


    @GetMapping("/board/save")
    public String insertBoard(Model model) {
        model.addAttribute("board", new BoardDto());
        return "board/board-add";
    }



    @GetMapping("/board-detail")
    public String detail(@RequestParam(value = "boardNo", required = false, defaultValue = "0") int boardNo,
                         Model model, Authentication authentication) {
        log.info("boardNo: " + boardNo);
        EmployeePrincipal employee = (EmployeePrincipal) authentication.getPrincipal();
        boardService.increaseBoardHit(boardNo);
        BoardDto boardDto = boardService.loadBoardByNo(boardNo, employee.getEmpNo());
        model.addAttribute("board", boardDto);
        model.addAttribute("files", boardDto.getFiles());

        List<CommentDto> comments = commentService.getComments(boardNo);
        model.addAttribute("comments", comments);
        model.addAttribute("currentEmpNo", employee.getEmpNo()); // 현재 로그인한 사용자
        log.info("comments: " + comments);
        log.info("currentEmpNo: {}", employee.getEmpNo());
        log.info(boardDto.toString());
        return "board/board-detail";
    }

    @GetMapping("/board-modify/{boardNo}")
    public String modify(@PathVariable int boardNo,
                         Model model, Authentication authentication) {
        log.info("boardNo: " + boardNo);
        EmployeePrincipal employee = (EmployeePrincipal) authentication.getPrincipal();
        BoardDto boardDto = boardService.loadBoardByNo(boardNo, employee.getEmpNo());
        model.addAttribute("files", boardDto.getFiles());
        model.addAttribute("board", boardDto);
        return "board/board-modify";
    }
}
