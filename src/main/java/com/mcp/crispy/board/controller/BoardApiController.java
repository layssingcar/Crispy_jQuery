package com.mcp.crispy.board.controller;

import com.mcp.crispy.auth.domain.EmployeePrincipal;
import com.mcp.crispy.board.dto.BoardDto;
import com.mcp.crispy.board.dto.BoardFileDto;
import com.mcp.crispy.board.service.BoardFileService;
import com.mcp.crispy.board.service.BoardService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/board")
public class BoardApiController {
    private final BoardService boardService;
    private final BoardFileService boardFileService;

    @PostMapping("/v1")
    public ResponseEntity<?> insertBoard(@Valid @RequestPart BoardDto boardDto,
                                         @RequestPart(required = false) List<MultipartFile> files,
                                         Authentication authentication) {
        EmployeePrincipal employee = (EmployeePrincipal) authentication.getPrincipal();
        int boardNo = boardService.insertBoard(boardDto, employee.getEmpNo(), files);
        return ResponseEntity.ok(Map.of("message", "게시글이 등록되었습니다.", "boardNo", boardNo));
    }


    @PutMapping("/v1")
    public ResponseEntity<?> modifyBoard(@Valid @RequestPart BoardDto boardDto,
                                         @RequestPart(required = false) List<MultipartFile> files,
                                         @RequestPart(required = false) List<Integer> deletedFileNo,
                                         Authentication authentication) {
        EmployeePrincipal employee = (EmployeePrincipal) authentication.getPrincipal();
        log.info("boardDto : {}",boardDto.toString());
        int boardNo = boardService.updateBoard(boardDto, employee.getEmpNo(), deletedFileNo, files);
        return ResponseEntity.ok(Map.of("message", "게시글이 수정되었습니다.", "boardNo", boardNo));
    }

    //게시판 삭제
    @DeleteMapping("/{boardNo}/v1")
    public ResponseEntity<?> deleteBoard(@PathVariable Integer boardNo,
                                         Authentication authentication) {
        log.info("deleteBoard boardNo : {}",boardNo);
        EmployeePrincipal employee = (EmployeePrincipal) authentication.getPrincipal();
        boardService.deleteBoard(boardNo, employee.getEmpNo());
        return ResponseEntity.ok(Map.of("message", "게시판이 삭제되었습니다."));
    }

    // 좋아요 토글
    @PostMapping("/{boardNo}/like/v1")
    public ResponseEntity<?> toggleLike(@PathVariable Integer boardNo,
                                        Authentication authentication) {
        EmployeePrincipal employee = (EmployeePrincipal) authentication.getPrincipal();
        boardService.toggleBoardLike(boardNo, employee.getEmpNo());
        return ResponseEntity.ok(Map.of("message", "좋아요 상태가 변경되었습니다."));
    }

    @GetMapping("/file/download")
    public ResponseEntity<Resource> downloadFile(@RequestParam("boardFileNo") int boardFileNo, HttpServletRequest request) {
        try {
            BoardFileDto boardFileDto = boardFileService.getFileDownloadInfo(boardFileNo, request);

            if (boardFileDto == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=" + boardFileDto.getBoardRename());
            headers.add("Content-Length", String.valueOf(boardFileDto.getContentLength()));

            return new ResponseEntity<>(boardFileDto.getResource(), headers, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/file/downloadAll")
    public ResponseEntity<Resource> downloadAllFiles(@RequestParam("boardNo") int boardNo) {
        try {
            BoardFileDto boardFileDto = boardFileService.getAllFilesDownloadInfo(boardNo);

            if (boardFileDto == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=" + boardFileDto.getBoardRename());
            headers.add("Content-Length", String.valueOf(boardFileDto.getContentLength()));

            return new ResponseEntity<>(boardFileDto.getResource(), headers, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
