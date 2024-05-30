package com.mcp.crispy.board.controller;

import com.mcp.crispy.board.dto.BoardDto;
import com.mcp.crispy.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/crispy")
public class BoardController {

	private final BoardService boardService;

	@GetMapping("/board-list")
	public String boardList(Model model) {
		List<BoardDto> freeList = boardService.getFreeBoardList();
		model.addAttribute("freeList", freeList);
		return "board/board-list";
	}

	@GetMapping("/board-list2")
	public String boardList2(Model model) {
		List<BoardDto> notiList = boardService.getNotiBoardList();
		model.addAttribute("notiList", notiList);
		return "board/board-list2";
	}

	@GetMapping("/board-add")
	public String boardAdd() {
		return "board/board-add";
	}
	@GetMapping("/board-modify")
	public String boardModify() {
		return "board/board-modify";
	}
	@GetMapping("/board-detail")
	public String boardDetail() {
		return "board/board-detail";
	}
	@GetMapping("/board-detail2")
	public String boardDetail2() {
		return "board/board-detail2";
	}


}