package com.mcp.crispy.board.service;

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
        // Retrieve boardFileed files
        List<MultipartFile> files = multipartRequest.getFiles("files");

        // Count of successfully inserted board files
        int insertBoardFileCount = 0;

        // Iterate over boardFileed files and process them
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

//    // 자유게시판 LIST
//    public List<BoardDto> getFreeBoardList() {
//        return boardMapper.getFreeBoardList();
//    }

    @Transactional(readOnly = true)
    public List<BoardDto> getFreeBoardList(Integer page,int cnt, String search) {
        int totalCount = getTotalCount(search);
        int total = totalCount/cnt + ((totalCount%cnt>0) ? 1:0);
        int begin = (page - 1) * cnt + 1;
        int end = begin + cnt - 1;
        if (search == null) {
            search = ""; // Set default value if null
        }        Map<String,Object> map = Map.of("begin",begin, "end",end, "total", total, "search", search);

        return boardMapper.getFreeBoardList(map);
    }


    @Transactional(readOnly = true)
    public int getTotalCount(String search) {
        return boardMapper.getTotalCount(search);
    }




    public ResponseEntity<Resource> download(int boardFileNo, HttpServletRequest request) {
        BoardFileDto boardFile = boardMapper.getBoardFileByNo(boardFileNo);
        File file = new File(boardFile.getBoardPath(), boardFile.getBoardRename());
        Resource resource = new FileSystemResource(file);

        if (!resource.exists()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        String boardOrigin = boardFile.getBoardOrigin();
        String userAgent = request.getHeader("User-Agent");
        try {
            if (userAgent.contains("Trident")) {
                boardOrigin = URLEncoder.encode(boardOrigin, "UTF-8").replace("+", " ");
            } else if (userAgent.contains("Edg")) {
                boardOrigin = URLEncoder.encode(boardOrigin, "UTF-8");
            } else {
                boardOrigin = new String(boardOrigin.getBytes("UTF-8"), "ISO-8859-1");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        HttpHeaders responseHeader = new HttpHeaders();
        responseHeader.add("Content-Disposition", "boardFilement; filename=" + boardOrigin);
        responseHeader.add("Content-Length", String.valueOf(file.length()));

        return new ResponseEntity<>(resource, responseHeader, HttpStatus.OK);
    }

    public ResponseEntity<Resource> downloadAll(int boardNo, HttpServletRequest request) {
        List<BoardFileDto> boardFileList = boardMapper.getBoardFileList(boardNo);
        if (boardFileList.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        File tempDir = new File(System.getProperty("java.io.tmpdir"));
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }

        String tempFilename = "boardFiles_" + boardNo + ".zip";
        File tempFile = new File(tempDir, tempFilename);

        try (ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(tempFile))) {
            for (BoardFileDto boardFile : boardFileList) {
                ZipEntry zipEntry = new ZipEntry(boardFile.getBoardOrigin());
                zout.putNextEntry(zipEntry);

                try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(new File(boardFile.getBoardPath(), boardFile.getBoardRename())))) {
                    zout.write(in.readAllBytes());
                }

                zout.closeEntry();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Resource resource = new FileSystemResource(tempFile);
        HttpHeaders responseHeader = new HttpHeaders();
        responseHeader.add("Content-Disposition", "boardFilement; filename=" + tempFilename);
        responseHeader.add("Content-Length", String.valueOf(tempFile.length()));

        return new ResponseEntity<>(resource, responseHeader, HttpStatus.OK);
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
    @Transactional(readOnly=true)
    public void loadBoardByNo(int boardNo, Model model) {
        model.addAttribute("board", boardMapper.getBoardByNo(boardNo));
        model.addAttribute("boardFileList", boardMapper.getBoardFileList(boardNo));
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
        // 삭제할 첨부 파일 정보를 DB에서 가져오기
        BoardFileDto boardFile = boardMapper.getBoardFileByNo(boardFileNo);

        // 파일 삭제
        File file = new File(boardFile.getBoardPath(), boardFile.getBoardRename());
        if (file.exists()) {
            file.delete();
        }

        // DB에서 첨부 파일 삭제
        int deleteCount = boardMapper.deleteBoardFile(boardFileNo);

        return ResponseEntity.ok(Map.of("deleteCount", deleteCount));
    }

    public int removeBoard(int boardNo) {
        // Get the list of files associated with the board
        List<BoardFileDto> boardFileList = boardMapper.getBoardFileList(boardNo);

        // Delete each file from the filesystem
        if (boardFileList != null && !boardFileList.isEmpty()) {
            for (BoardFileDto boardFile : boardFileList) {
                File file = new File(boardFile.getBoardPath(), boardFile.getBoardRename());
                if (file.exists()) {
                    file.delete();
                }
            }
        }

        // Delete each file record from the database
        if (boardFileList != null && !boardFileList.isEmpty()) {
            for (BoardFileDto boardFile : boardFileList) {
                boardMapper.deleteBoardFile(boardFile.getBoardFileNo());
            }
        }

        // Delete the board from the database
        return boardMapper.deleteBoard(boardNo);
    }

    @Transactional
    public int updateHit(int boardNo) {
        return boardMapper.updateHit(boardNo);
    }


//    // 좋아요 여부 확인 서비스
//    public int boardLikeCheck(Map<String, Object> map) {
//        return BoardMapper.boardLikeCheck(map);
//    }

//    // 좋아요 처리 서비스
//    @Transactional(rollbackFor = Exception.class)
//
//    public int like(Map<String, Integer> paramMap) {
//        int result = 0;
//
//        if(paramMap.get("check") == 0) { // 좋아요 상태 X
//            // BOARD_LIKE 테이블 INSERT
//            result = boardMapper.insertBoardLike(paramMap);
//        } else { // 좋아요 상태 O
//            // BOARD_LIKE 테이블 DELETE
//            result = boardMapper.deleteBoardLike(paramMap);
//        }
//
//        // SQL 수행 결과가 0 == DB 또는 파라미터에 문제가 있다.
//        // 1) 에러를 나타내는 임의의 값을 반환 (-1)
//        if(result == 0) return -1;
//
//        // 현재 게시글의 좋아요 개수 조회
//        int count = boardMapper.countBoardLike(paramMap.get("boardNo"));
//
//        return count;
//    }

}