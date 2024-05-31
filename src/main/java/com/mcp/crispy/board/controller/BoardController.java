package com.mcp.crispy.board.controller;

import com.mcp.crispy.board.dto.BoardDto;
import com.mcp.crispy.board.service.BoardService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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
	public String boardDetail(BoardDto boardDto, Model model) {
		BoardDto board = boardService.getBoardByNo(boardDto.getBoardNo());
		model.addAttribute("board",board);
		return "board/board-detail";
	}
	@GetMapping("/board-detail2")
	public String boardDetail2(BoardDto boardDto,Model model) {
		BoardDto board = boardService.getBoardByNo(boardDto.getBoardNo());
		model.addAttribute("board",board);
		return "board/board-detail2";
	}

	@ResponseBody
	@DeleteMapping(value="/deleteBoard/{boardNo}", produces = "application/json")
	public int deleteBoard(@PathVariable(value = "boardNo")Optional<String> opt){
		int boardNo = Integer.parseInt(opt.orElse("0"));
		return boardService.deleteBoard(boardNo);
	}

	@GetMapping("/add")
	public String boardAdd(BoardDto boardDto, Model model){
		BoardDto board = boardService.getBoardByNo(boardDto.getBoardNo());
		model.addAttribute("board",board);
		return "board/add";
	}
	@PostMapping("/add-form")
	public String register(MultipartHttpServletRequest request,
						   RedirectAttributes redirectAttributes,
						   BoardDto boardDto) throws Exception{
		int boardId = boardService.registerBoard(request);
		if(boardId> 0){
			redirectAttributes.addFlashAttribute("insertResult","등록되었습니다.");
		return "redirect:/crispy/detail?boardNo=" +boardId;
		} else{
			redirectAttributes.addFlashAttribute("insertResult","등록되지 않았습니다.");
			return "redirect:/crispy/add";
		}
	}

	@PostMapping(value="/addBoardFile", produces="application/json")
	public ResponseEntity<Map<String, Object>> addAttach(MultipartHttpServletRequest multipartRequest) throws Exception {
		return boardService.addBoardFile(multipartRequest);
	}


	@GetMapping("/modify")
	public String boardModify(BoardDto boardDto,Model model){
		BoardDto board = boardService.getBoardByNo(boardDto.getBoardNo());
		model.addAttribute("board",board);
		return "board/modify";
	}


}