package com.mcp.crispy.board.controller;

import com.mcp.crispy.auth.domain.EmployeePrincipal;
import com.mcp.crispy.board.dto.BoardDto;
import com.mcp.crispy.board.dto.BoardFileDto;
import com.mcp.crispy.board.mapper.BoardMapper;
import com.mcp.crispy.board.service.BoardService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@RequestMapping("/crispy")
@RequiredArgsConstructor
@Controller
public class BoardController {

	private final BoardService boardService;
	private final BoardMapper boardMapper;

//	@GetMapping("/list")
//	public String list(HttpServletRequest request, Model model) {
//		model.addAttribute("request", request);
//	boardService.loadBoardList(model);
//		return "board/board-list";
//}

	/**
	 * 오정은 - 자유게시판 LIST
	 *
	 * @param model
	 * @return forward (board/board-list.html)
	 */
	@GetMapping("/board-list")
	public String freeList(@RequestParam(value = "page", required = false) Integer page, Model model
			, @RequestParam(value = "search", required = false) String search) {

		if(page == null) {
			page = 1;
		}

		if (search == null) {
			search = ""; // Set default value if null
		}
		// 전체 게시물 리스트
		List<BoardDto> freeList = boardService.getFreeBoardList(page, 10, search);
		model.addAttribute("freeList", freeList);

		// 해당 게시물 전체 조회
		for(BoardDto b : freeList)
			System.out.println(b);

		// 전체 게시물 수 조회
		int totalCount = boardService.getTotalCount(search);
		model.addAttribute("totalCount", totalCount);
		// 전체 게시물 / 10
		int maxPage = (int)Math.ceil((double)totalCount/10);

		int pageShow = 10;
		int startPage = ((page - 1) / pageShow) * pageShow + 1;
		int endPage = startPage + pageShow - 1;

		// 다음 페이지, 이전 페이지 계산
		int nextPage = Math.min(page + 10, maxPage);
		int prevPage = Math.max(page - 10, 1);
		model.addAttribute("nextPage", nextPage);
		model.addAttribute("prevPage", prevPage);

		// 시작번호, 끝번호 계산 후 표출
		endPage = Math.min(endPage, maxPage);
		startPage = Math.max(startPage, 1);

		model.addAttribute("currentPage", page);
		model.addAttribute("maxPage", maxPage);
		model.addAttribute("startPage",startPage);
		model.addAttribute("endPage",endPage);

		return "board/board-list";
	}


	@GetMapping("/board-add")
	public String boardAdd(Model model, Authentication authentication) {
		// authentication에 가맹점 번호, 직원 번호, 아이디, 비밀번호, 권한 들어가있음
		EmployeePrincipal principal = (EmployeePrincipal) authentication.getPrincipal();

		model.addAttribute("empNo", principal.getEmpNo());

		return "board/board-add";
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
	public ResponseEntity<Resource> download(@RequestParam("boardFileNo") int boardFileNo, HttpServletRequest request) {
		return boardService.download(boardFileNo, request);
	}

	@GetMapping("/downloadAll")
	public ResponseEntity<Resource> downloadAll(@RequestParam("boardNo") int boardNo, HttpServletRequest request) {
		return boardService.downloadAll(boardNo, request);
	}


	@GetMapping("/board-modify")
	public String edit(@RequestParam int boardNo, Model model, Authentication authentication) {
		EmployeePrincipal principal = (EmployeePrincipal) authentication.getPrincipal();
		model.addAttribute("empNo", principal.getEmpNo());
		model.addAttribute("board", boardService.getBoardByNo(boardNo));
		model.addAttribute("boardFileList", boardService.getBoardFileList(boardNo).getBody().get("boardFileList"));
		return "board/board-modify";
	}


	@PostMapping("/modify-form")
	public String modify(BoardDto board, RedirectAttributes redirectAttributes) {
		// empNo를 boardDto에 설정
		redirectAttributes
				.addAttribute("boardNo", board.getBoardNo())
				.addFlashAttribute("modifyResult", boardService.modifyBoard(board) == 1 ? "수정되었습니다." : "수정을 하지 못했습니다.");
		return "redirect:/crispy/board-detail?boardNo={boardNo}";
	}


	@GetMapping(value = "/boardFileList", produces = "application/json")
	public ResponseEntity<Map<String, Object>> boardFileList(@RequestParam int boardNo, Model model) {
		return boardService.getBoardFileList(boardNo);
	}

	@PostMapping(value = "/addBoardFile", produces = "application/json")
	public ResponseEntity<Map<String, Object>> addBoardFile(MultipartHttpServletRequest multipartRequest) throws Exception {
		return boardService.addBoardFile(multipartRequest);
	}

	@PostMapping(value = "/removeBoardFile", produces = "application/json")
	public ResponseEntity<Map<String, Object>> removeBoardFile(@RequestBody BoardFileDto boardFile) {
		return boardService.removeBoardFile(boardFile.getBoardFileNo());
	}

	@PostMapping("/removeBoard")
	public String removeBoard(@RequestParam(value = "boardNo", required = false, defaultValue = "0") int boardNo, RedirectAttributes redirectAttributes) {
		// boardNo가 0 이하이거나 유효하지 않으면 삭제하지 않고 페이지를 리다이렉트합니다.
		if (boardNo <= 0) {
			redirectAttributes.addFlashAttribute("removeResult", "삭제할 게시물을 선택해주세요.");
			return "redirect:/crispy/board-list";
		}

		// 게시물과 관련된 파일을 먼저 삭제합니다.
		boardService.removeBoardFile(boardNo);

		// 게시물을 삭제합니다.
		int removeCount = boardService.removeBoard(boardNo);

		// 삭제 결과에 따라 메시지를 설정합니다.
		redirectAttributes.addFlashAttribute("removeResult", removeCount > 0 ? "게시물이 삭제되었습니다." : "게시물 삭제를 실패했습니다.");

		// 게시물 목록 페이지로 리다이렉트합니다.
		return "redirect:/crispy/board-list";
	}

	@ResponseBody
	@GetMapping("/putBoardHit")
	public void updateHit(@RequestParam("boardNo") int boardNo, HttpServletResponse response) {
		int updatedHits = boardService.updateHit(boardNo);
		if (updatedHits > 0) {
			response.setStatus(HttpServletResponse.SC_OK);
		} else {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}


//	@PostMapping("/putBoardHit")
//	public void putBoardHit(@RequestBody Map<String, Integer> request, HttpServletResponse response) {
//		int boardNo = request.get("boardNo");
//		boardService.updateHit(boardNo);
//		response.setStatus(HttpServletResponse.SC_OK); // 성공 상태코드 설정
//	}


//	// 좋아요 처리
//	@PostMapping("/like")
//	@ResponseBody // 반환되는 값이 비동기 요청한 곳으로 돌아가게 함
//	public int like(@RequestBody Map<String, Integer> paramMap) {
//		// System.out.println(paramMap);
//
//		return boardService.like(paramMap);
//	}


}