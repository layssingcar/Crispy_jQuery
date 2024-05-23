package com.mcp.crispy.board.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/board")
public class BoardController {
	
	@GetMapping("/board-list")
	public String boardList() {
		return "board/board-list";
	}
	@GetMapping("/board-list2")
	public String boardList2() {
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
