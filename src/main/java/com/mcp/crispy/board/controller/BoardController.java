package com.mcp.crispy.board.controller;

import java.util.List;
import java.util.Map;

import com.mcp.crispy.auth.domain.EmployeePrincipal;
import com.mcp.crispy.board.dto.BoardDto;
import com.mcp.crispy.board.dto.BoardFileDto;
import com.mcp.crispy.board.service.BoardService;
import jakarta.servlet.http.HttpServletRequest;

import jakarta.servlet.http.HttpSession;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import lombok.RequiredArgsConstructor;

@RequestMapping("/crispy")
@RequiredArgsConstructor
@Controller
public class BoardController {

	private final BoardService boardService;

//	@GetMapping("/list")
//	public String list(HttpServletRequest request, Model model) {
//		model.addAttribute("request", request);
//		boardService.loadBoardList(model);
//		return "board/board-list";
//	}

	/**
	 * 오정은 - 자유게시판 LIST
	 * @param model
	 * @return forward (board/board-list.html)
	 */
	@GetMapping("/board-list")
	public String boardList(Model model) {
		List<BoardDto> freeList = boardService.getFreeBoardList();
		model.addAttribute("freeList", freeList);
		return "board/board-list";
	}


	@GetMapping("/board-add")
	public String boardAdd(Model model, HttpSession session, Authentication authentication){
		// authentication에 가맹점 번호, 직원 번호, 아이디, 비밀번호, 권한 들어가있음
//		EmployeePrincipal principal = (EmployeePrincipal) authentication.getPrincipal();

		String empNo = (String) session.getAttribute("empNo");
		if (empNo == null) {
			empNo = "1"; // 기본 empNo 설정 또는 적절한 처리
		}
		model.addAttribute("inserted", new Inserted(empNo));

		return "board/board-add";
	}

	public static class Inserted {
		private String empNo;

		public Inserted(String empNo) {
			this.empNo = empNo;
		}

		public String getEmpNo() {
			return empNo;
		}

		public void setEmpNo(String empNo) {
			this.empNo = empNo;
		}
	}



	@PostMapping("/add-form")
	public String boardAddForm(MultipartHttpServletRequest multipartRequest, RedirectAttributes redirectAttributes) {
		String boardTitle = multipartRequest.getParameter("boardTitle");
		String boardContent = multipartRequest.getParameter("boardContent");
		int empNo = Integer.parseInt(multipartRequest.getParameter("empNo"));
		int boardCtNo = Integer.parseInt(multipartRequest.getParameter("boardCtNo"));

		// 게시글 등록
		int boardNo = boardService.registerBoard(boardTitle, boardContent, empNo, boardCtNo);

		// 첨부 파일 등록
		boolean inserted = boardService.registerBoardFile(multipartRequest, boardNo);

		redirectAttributes.addFlashAttribute("inserted", inserted);
		return "redirect:/crispy/board-list";
	}

	@GetMapping("/board-detail")
	public String detail(@RequestParam(value = "boardNo", required = false, defaultValue = "0") int boardNo, Model model) {
		boardService.loadBoardByNo(boardNo, model);
		return "board/board-detail";
	}


	@GetMapping("/download")
	public ResponseEntity<Resource> download(HttpServletRequest request) {
		return boardService.download(request);
	}

	@GetMapping(value="/downloadAll", produces="application/octet-stream")
	public ResponseEntity<Resource> downloadAll(HttpServletRequest request) {
		return boardService.downloadAll(request);
	}

	@PostMapping("/board-modify")
	public String edit(@RequestParam int boardNo, Model model) {
		model.addAttribute("board", boardService.getBoardByNo(boardNo));
		return "board/board-modify";
	}

	@PostMapping("/modify-form")
	public String modify(BoardDto board, RedirectAttributes redirectAttributes) {
		redirectAttributes
				.addAttribute("boardNo", board.getBoardNo())
				.addFlashAttribute("modifyResult", boardService.modifyBoard(board) == 1 ? "수정되었습니다." : "수정을 하지 못했습니다.");
		return "redirect:/crispy/board-detail?boardNo={boardNo}";
	}

	@GetMapping(value="/boardFileList", produces="application/json")
	public ResponseEntity<Map<String, Object>> boardFileList(@RequestParam int boardNo) {
		return boardService.getBoardFileList(boardNo);
	}

	@PostMapping(value="/addBoardFile", produces="application/json")
	public ResponseEntity<Map<String, Object>> addBoardFile(MultipartHttpServletRequest multipartRequest) throws Exception {
		return boardService.addBoardFile(multipartRequest);
	}

	@PostMapping(value="/removeBoardFile", produces="application/json")
	public ResponseEntity<Map<String, Object>> removeBoardFile(@RequestBody BoardFileDto boardFile) {
		return boardService.removeBoardFile(boardFile.getBoardFileNo());
	}

	@PostMapping("/removeBoard")
	public String removeBoard(@RequestParam(value="boardNo", required=false, defaultValue="0") int boardNo
			, RedirectAttributes redirectAttributes) {
		int removeCount = boardService.removeBoard(boardNo);
		redirectAttributes.addFlashAttribute("removeResult", removeCount == 1 ? "삭제되었습니다." : "삭제를 하지 못했습니다.");
		return "redirect:/crispy/board-list";
	}

}