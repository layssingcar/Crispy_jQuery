package com.mcp.crispy.board.service;

import com.mcp.crispy.board.dto.BoardDto;
import com.mcp.crispy.board.dto.BoardFileDto;
import com.mcp.crispy.board.mapper.BoardMapper;
import com.mcp.crispy.board.utils.MyFileUtils;
import com.mcp.crispy.employee.dto.EmployeeDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.File;
import java.util.List;
import java.util.Map;

@Service
//@RequiredArgsConstructor
public class BoardService {

    @Autowired
    private BoardMapper boardMapper;
    @Autowired
    private MyFileUtils myFileUtils;

    public ResponseEntity<Map<String, Object>> addBoardFile(MultipartHttpServletRequest multipartRequest) throws Exception {

        List<MultipartFile> files = multipartRequest.getFiles("files");

        int boardFileCount = 0;
        if (files.isEmpty() || files.get(0).getSize() == 0) {
            boardFileCount = 1;
        }

        for (MultipartFile multipartFile : files) {

            if (multipartFile != null && !multipartFile.isEmpty()) {

                String boardPath = myFileUtils.getBoardPath();
                File dir = new File(boardPath);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                String boardOrigin = multipartFile.getOriginalFilename();
                String boardRename = myFileUtils.getBoardRename(boardOrigin);
                File file = new File(dir, boardRename);

                multipartFile.transferTo(file);

                BoardFileDto boardFile = BoardFileDto.builder()
                        .boardPath(boardPath)
                        .boardOrigin(boardOrigin)
                        .boardRename(boardRename)
                        .boardNo(Integer.parseInt(multipartRequest.getParameter("boardNo")))
                        .build();

                boardFileCount += boardMapper.insertBoardFile(boardFile);
            }
        }

        return ResponseEntity.ok(Map.of("attachResult", files.size() == boardFileCount));
    }

    public List<BoardDto> getFreeBoardList() {
        return boardMapper.getFreeBoardList();
    }

    public List<BoardDto> getNotiBoardList() {
        return boardMapper.getNotiBoardList();
    }

    public BoardDto getBoardByNo(int boardNo) {
        return boardMapper.getBoardByNo(boardNo);
    }

    public int deleteBoard(int boardNo) {
        return boardMapper.deleteBoard(boardNo);
    }

    public int registerBoard(MultipartHttpServletRequest multipartRequest) throws Exception {
        String title = multipartRequest.getParameter("boardTitle");
        String content = multipartRequest.getParameter("boardContent");
        int boardCtNo = Integer.parseInt(multipartRequest.getParameter("boardCtNo"));
        int empNo = Integer.parseInt(multipartRequest.getParameter("empNo"));

        EmployeeDto employee = new EmployeeDto();
        employee.setEmpNo(empNo);
        BoardDto board = BoardDto.builder()
                .boardCtNo(boardCtNo)
                .boardTitle(title)
                .boardContent(content)
                .empNo(empNo)
                .build();

        int insertBoardCount = boardMapper.insertBoard(board);

        List<MultipartFile> files = multipartRequest.getFiles("files");

        int insertBoardFileCount = 0;
        if (files.isEmpty() || files.get(0).getSize() == 0) {
            insertBoardFileCount = 1;
        }

        for (MultipartFile multipartFile : files) {

            if (multipartFile != null && !multipartFile.isEmpty()) {

                String boardPath = myFileUtils.getBoardPath();
                File dir = new File(boardPath);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                String boardOrigin = multipartFile.getOriginalFilename();
                String boardRename = myFileUtils.getBoardRename(boardOrigin);
                File file = new File(dir, boardRename);

                multipartFile.transferTo(file);

                BoardFileDto boardFile = BoardFileDto.builder()
                        .boardPath(boardPath)
                        .boardRename(boardRename)
                        .boardOrigin(boardOrigin)
                        .boardNo(board.getBoardNo())
                        .build();

                insertBoardFileCount += boardMapper.insertBoardFile(boardFile);
            }
        }

        return (insertBoardCount == 1 && insertBoardFileCount == files.size()) ? board.getBoardNo() : -1;
    }
}