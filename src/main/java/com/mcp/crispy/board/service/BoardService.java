package com.mcp.crispy.board.service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.mcp.crispy.board.dto.BoardDto;
import com.mcp.crispy.board.dto.BoardFileDto;
import com.mcp.crispy.board.mapper.BoardMapper;
import com.mcp.crispy.board.utils.MyFileUtils;
import jakarta.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@Transactional
@RequiredArgsConstructor
@Service
public class BoardService {

    private final BoardMapper boardMapper;
    private final MyFileUtils myFileUtils;

    @Transactional
    public int registerBoard(String title, String content, int empNo, int boardCtNo) {
        // Create BoardDto instance
        BoardDto board = BoardDto.builder()
                .boardCtNo(boardCtNo) // Assuming 자유게시판 카테고리가 1로 정의되어 있음
                .boardTitle(title)
                .boardContent(content)
                .empNo(empNo)
                .build();

        // Insert board and retrieve generated boardNo
        int insertBoardCount = boardMapper.insertBoard(board);

        // Check if board was successfully inserted
        if (insertBoardCount == 1) {
            // Retrieve and return generated boardNo
            return board.getBoardNo();
        }

        // Return -1 if board insertion fails
        return -1;
    }

    // BoardService 클래스에 registerBoardFile 메서드 수정
    @Transactional
    public boolean registerBoardFile(MultipartHttpServletRequest multipartRequest, int boardNo) {
        // Retrieve attached files
        List<MultipartFile> files = multipartRequest.getFiles("files");

        // Count of successfully inserted board files
        int insertBoardFileCount = 0;

        // Iterate over attached files and process them
        for (MultipartFile multipartFile : files) {
            if (multipartFile != null && !multipartFile.isEmpty()) {
                try {
                    // Save the file to disk
                    String boardPath = myFileUtils.getBoardPath();
                    File dir = new File(boardPath);
                    if (!dir.exists()) {
                        dir.mkdirs();  // Create directories if they do not exist
                    }

                    String boardOrigin = multipartFile.getOriginalFilename();
                    String boardRename = myFileUtils.getBoardRename(boardOrigin);
                    File file = new File(boardPath, boardRename);
                    multipartFile.transferTo(file);

                    // Create BoardFileDto instance
                    BoardFileDto boardFile = BoardFileDto.builder()
                            .boardRename(boardRename)
                            .boardOrigin(boardOrigin)
                            .boardPath(boardPath)
                            .boardNo(boardNo) // Use the generated boardNo
                            .build();

                    // Insert board file
                    int inserted = boardMapper.insertBoardFile(boardFile);
                    if (inserted == 1) {
                        insertBoardFileCount++;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // Ensure all files were successfully inserted
        return insertBoardFileCount == files.size();
    }

//    @Transactional(readOnly=true)
//    public void loadBoardList(Model model) {
//
//
//        Map<String, Object> modelMap = model.asMap();
//        HttpServletRequest request = (HttpServletRequest) modelMap.get("request");
//
//        int total = boardMapper.getBoardCount();
//
//        Optional<String> optDisplay = Optional.ofNullable(request.getParameter("display"));
//        int display = Integer.parseInt(optDisplay.orElse("20"));
//
//        Optional<String> optPage = Optional.ofNullable(request.getParameter("page"));
//        int page = Integer.parseInt(optPage.orElse("1"));
//
//        myPageUtils.setPaging(total, display, page);
//
//        Optional<String> optSort = Optional.ofNullable(request.getParameter("sort"));
//        String sort = optSort.orElse("DESC");
//
//        Map<String, Object> map = Map.of("begin", myPageUtils.getBegin()
//                , "end", myPageUtils.getEnd()
//                , "sort", sort);
//
//        /*
//         * total = 100, display = 20
//         *
//         * page  beginNo
//         * 1     100
//         * 2     80
//         * 3     60
//         * 4     40
//         * 5     20
//         */
//        model.addAttribute("beginNo", total - (page - 1) * display);
//        model.addAttribute("boardList", boardMapper.getBoardList(map));
//        model.addAttribute("paging", myPageUtils.getPaging(request.getContextPath() + "/board/list.do", sort, display));
//        model.addAttribute("display", display);
//        model.addAttribute("sort", sort);
//        model.addAttribute("page", page);
//
//    }

    // 자유게시판 LIST
    public List<BoardDto> getFreeBoardList() {
        return boardMapper.getFreeBoardList();
    }


    @Transactional(readOnly=true)
    public void loadBoardByNo(int boardNo, Model model) {
        model.addAttribute("board", boardMapper.getBoardByNo(boardNo));
        model.addAttribute("boardFileList", boardMapper.getBoardFileList(boardNo));
    }



    public ResponseEntity<Resource> download(HttpServletRequest request) {

        // 첨부 파일 정보를 DB 에서 가져오기
        int boardFileNo = Integer.parseInt(request.getParameter("boardFileNo"));
        BoardFileDto boardFile = boardMapper.getBoardFileByNo(boardFileNo);

        // 첨부 파일 정보를 File 객체로 만든 뒤 Resource 객체로 변환
        File file = new File(boardFile.getBoardPath(), boardFile.getBoardRename());
        Resource resource = new FileSystemResource(file);

        // 첨부 파일이 없으면 다운로드 취소
        if(!resource.exists()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // DOWNLOAD_COUNT 증가
//        boardMapper.updateDownloadCount(boardFileNo);

        // 사용자가 다운로드 받을 파일명 결정 (boardOrigin 을 브라우저에 따라 다르게 인코딩 처리)
        String boardOrigin = boardFile.getBoardOrigin();
        String employeeAgent = request.getHeader("Employee-Agent");
        try {
            // IE
            if(employeeAgent.contains("Trident")) {
                boardOrigin = URLEncoder.encode(boardOrigin, "UTF-8").replace("+", " ");
            }
            // Edge
            else if(employeeAgent.contains("Edg")) {
                boardOrigin = URLEncoder.encode(boardOrigin, "UTF-8");
            }
            // Other
            else {
                boardOrigin = new String(boardOrigin.getBytes("UTF-8"), "ISO-8859-1");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 다운로드용 응답 헤더 설정 (HTTP 참조)
        HttpHeaders responseHeader = new HttpHeaders();
        responseHeader.add("Content-Type", "application/octet-stream");
        responseHeader.add("Content-Disposition", "boardFilement; filename=" + boardOrigin);
        responseHeader.add("Content-Length", file.length() + "");

        // 다운로드 진행
        return new ResponseEntity<Resource>(resource, responseHeader, HttpStatus.OK);

    }

    public ResponseEntity<Resource> downloadAll(HttpServletRequest request) {

        // 다운로드 할 모든 첨부 파일들의 정보를 DB 에서 가져오기
        int boardNo = Integer.parseInt(request.getParameter("boardNo"));
        List<BoardFileDto> boardFileList = boardMapper.getBoardFileList(boardNo);

        // 첨부 파일이 없으면 종료
        if(boardFileList.isEmpty()) {
            return new ResponseEntity<Resource>(HttpStatus.NOT_FOUND);
        }

        // 임시 zip 파일 저장할 경로
        File tempDir = new File(myFileUtils.getTempPath());
        if(!tempDir.exists()) {
            tempDir.mkdirs();
        }

        // 임시 zip 파일 이름
        String tempFilename = myFileUtils.getTempFilename() + ".zip";

        // 임시 zip 파일 File 객체
        File tempFile = new File(tempDir, tempFilename);

        // 첨부 파일들을 하나씩 zip 파일로 모으기
        try {

            // ZipOutputStream 객체 생성
            ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(tempFile));

            for (BoardFileDto boardFile : boardFileList) {

                // zip 파일에 포함할 ZipEntry 객체 생성
                ZipEntry zipEntry = new ZipEntry(boardFile.getBoardOrigin());

                // zip 파일에 ZipEntry 객체 명단 추가 (파일의 이름만 등록한 상황)
                zout.putNextEntry(zipEntry);

                // 실제 첨부 파일을 zip 파일에 등록 (첨부 파일을 읽어서 zip 파일로 보냄)
                BufferedInputStream in = new BufferedInputStream(new FileInputStream(new File(boardFile.getBoardPath(), boardFile.getBoardRename())));
                zout.write(in.readAllBytes());

                // 사용한 자원 정리
                in.close();
                zout.closeEntry();

                // DOWNLOAD_COUNT 증가
//                boardMapper.updateDownloadCount(boardFile.getBoardFileNo());

            }  // for

            // zout 자원 반납
            zout.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        // 다운로드 할 zip File 객체 -> Resource 객체
        Resource resource = new FileSystemResource(tempFile);

        // 다운로드용 응답 헤더 설정 (HTTP 참조)
        HttpHeaders responseHeader = new HttpHeaders();
        responseHeader.add("Content-Disposition", "boardFilement; filename=" + tempFilename);
        responseHeader.add("Content-Length", tempFile.length() + "");

        // 다운로드 진행
        return new ResponseEntity<Resource>(resource, responseHeader, HttpStatus.OK);

    }


    public void removeTempFiles() {
        File tempDir = new File(myFileUtils.getTempPath());
        File[] tempFiles = tempDir.listFiles();
        if(tempFiles != null) {
            for(File tempFile : tempFiles) {
                tempFile.delete();
            }
        }
    }

    @Transactional(readOnly=true)

    public BoardDto getBoardByNo(int boardNo) {
        return boardMapper.getBoardByNo(boardNo);
    }


    public int modifyBoard(BoardDto board) {
        return boardMapper.updateBoard(board);
    }

    @Transactional(readOnly=true)

    public ResponseEntity<Map<String, Object>> getBoardFileList(int boardNo) {
        return ResponseEntity.ok(Map.of("boardFileList", boardMapper.getBoardFileList(boardNo)));
    }

    public ResponseEntity<Map<String, Object>> addBoardFile(MultipartHttpServletRequest multipartRequest) throws Exception {

        List<MultipartFile> files =  multipartRequest.getFiles("files");

        int boardFileCount;
        if(files.get(0).getSize() == 0) {
            boardFileCount = 1;
        } else {
            boardFileCount = 0;
        }

        for(MultipartFile multipartFile : files) {

            if(multipartFile != null && !multipartFile.isEmpty()) {

                String boardPath = myFileUtils.getBoardPath();
                File dir = new File(boardPath);
                if(!dir.exists()) {
                    dir.mkdirs();
                }

                String boardOrigin = multipartFile.getOriginalFilename();
                String boardRename = myFileUtils.getBoardRename(boardOrigin);
                File file = new File(dir, boardRename);

                multipartFile.transferTo(file);

                String contentType = Files.probeContentType(file.toPath());  // 이미지의 Content-Type은 image/jpeg, image/png 등 image로 시작한다.

                BoardFileDto boardFile = BoardFileDto.builder()
                        .boardOrigin(boardOrigin)
                        .boardRename(boardRename)
                        .boardNo(Integer.parseInt(multipartRequest.getParameter("boardNo")))
                        .boardPath(boardPath)
                        .build();

                boardFileCount += boardMapper.insertBoardFile(boardFile);

            }  // if

        }  // for

        return ResponseEntity.ok(Map.of("boardFileResult", files.size() == boardFileCount));

    }

    public ResponseEntity<Map<String, Object>> removeBoardFile(int boardFileNo) {

        // 삭제할 첨부 파일 정보를 DB 에서 가져오기
        BoardFileDto boardFile = boardMapper.getBoardFileByNo(boardFileNo);

        // 파일 삭제
        File file = new File(boardFile.getBoardPath(), boardFile.getBoardRename());
        if(file.exists()) {
            file.delete();
        }


        // DB 삭제
        int deleteCount = boardMapper.deleteBoardFile(boardFileNo);

        return ResponseEntity.ok(Map.of("deleteCount", deleteCount));

    }

    public int removeBoard(int boardNo) {

        // 파일 삭제
        List<BoardFileDto> boardFileList = boardMapper.getBoardFileList(boardNo);
        for(BoardFileDto boardFile : boardFileList) {

            File file = new File(boardFile.getBoardPath(), boardFile.getBoardRename());
            if(file.exists()) {
                file.delete();
            }

        }

        // UPLOAD_T 삭제
        return boardMapper.deleteBoard(boardNo);

    }

}