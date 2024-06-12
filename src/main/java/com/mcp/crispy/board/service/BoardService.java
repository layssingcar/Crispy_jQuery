package com.mcp.crispy.board.service;

import com.mcp.crispy.board.dto.BoardDto;
import com.mcp.crispy.board.dto.BoardFileDto;
import com.mcp.crispy.board.mapper.BoardMapper;
import com.mcp.crispy.common.page.PageResponse;
import com.mcp.crispy.common.utils.MyFileUtils;
import com.vane.badwordfiltering.BadWordFiltering;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardMapper boardMapper;
    private final MyFileUtils myFileUtils;
    private final BadWordFiltering badWordFiltering;

    // 게시판 생성
    @Transactional
    public int insertBoard(BoardDto boardDto, Integer empNo, List<MultipartFile> files) {

        BoardDto board = BoardDto.builder()
                .empNo(empNo)
                .boardTitle(boardDto.getBoardTitle())
                .boardContent(boardDto.getBoardContent())
                .boardCtNo(boardDto.getBoardCtNo())
                .build();
        boardMapper.insertBoard(board);

        log.info("insertBoard: {}", board.getBoardCtNo());

        if (files != null && !files.isEmpty()) {
            registerBoardFile(files, board.getBoardNo());
        }
        return board.getBoardNo();
    }

    // 게시판 수정
    @Transactional
    public int updateBoard(BoardDto boardDto, Integer empNo, List<Integer> deletedFileNo, List<MultipartFile> newFiles) {

        BoardDto board = BoardDto.builder()
                .boardNo(boardDto.getBoardNo())
                .boardTitle(boardDto.getBoardTitle())
                .boardContent(boardDto.getBoardContent())
                .boardCtNo(boardDto.getBoardCtNo())
                .modifier(empNo)
                .build();
        boardMapper.updateBoard(board);

        if (deletedFileNo != null && !deletedFileNo.isEmpty()) {
            for (Integer fileNo : deletedFileNo) {
                BoardFileDto file = boardMapper.getBoardFileByNo(fileNo);
                if (file != null) {
                    File fileToDelete = new File(file.getBoardPath(), file.getBoardRename());
                    if (fileToDelete.exists()) {
                        fileToDelete.delete();
                    }
                    boardMapper.deleteBoardFile(fileNo);
                }
            }
        }
        // 새로운 파일 추가
        if (newFiles != null && !newFiles.isEmpty()) {
            registerBoardFile(newFiles, board.getBoardNo());
        }
        return board.getBoardNo();
    }

    // 게시판 삭제
    public void deleteBoard(Integer boardNo, Integer modifier) {
        log.info("Delete Board : {} {}", boardNo, modifier);
        boardMapper.deleteBoard(boardNo, modifier);
    }

    // 자유게시판 LIST
    public PageResponse<BoardDto> getFreeBoardList(BoardDto boardDto, int limit) {
        // 현재 페이지 번호
        int page = Math.max(boardDto.getPageNo(), 1);

        // 전체 재고 항목 수
        int totalCount = boardMapper.getBoardCount();

        // 전체 페이지 수
        int totalPage = totalCount / limit + ((totalCount % limit > 0) ? 1 : 0);

        // 페이지 내비게이션 범위
        int startPage = ((page - 1) / limit) * limit + 1;
        int endPage = Math.min(startPage + limit - 1, totalPage);

        /*
         * 조회 범위
         *  - offset: 조회를 시작할 행의 인덱스
         *  - limit: 조회할 행의 개수
         */
        RowBounds rowBounds = new RowBounds(limit * (page - 1), limit);

        // 게시판 리스트
        List<BoardDto> items = boardMapper.getFreeBoardList(boardDto, rowBounds);
        items.forEach(item -> {
            String boardTitle = item.getBoardTitle();
            String boardContent = item.getBoardContent();
            item.setBoardTitle(boardTitle);
            item.setBoardContent(boardContent);
        });

        // PageResponse 객체
        return new PageResponse<>(items, totalPage, startPage, endPage, page);
    }

    @Transactional(readOnly=true)
    public BoardDto loadBoardByNo(int boardNo, int empNo) {
        log.info("Load board by no: {}", boardNo);
        BoardDto boardDto = boardMapper.getBoardByNo(boardNo);
        log.info("Load Board : {}", boardDto);
        List<BoardFileDto> files = boardMapper.getBoardFileList(boardNo);

        // 게시판 제목과 내용을 필터링
        boardDto.setBoardTitle(badWordFiltering.change(boardDto.getBoardTitle()));
        boardDto.setBoardContent(badWordFiltering.change(boardDto.getBoardContent()));

        boardDto.setFiles(files);

        boolean isLiked = boardMapper.isLiked(boardNo, empNo) > 0;
        boardDto.setLiked(isLiked);

        return boardDto;
    }



    // BoardService 클래스에 registerBoardFile 메서드 수정
    @Transactional
    public boolean registerBoardFile(List<MultipartFile> files, int boardNo) {
        int insertBoardFileCount = 0;
        for (MultipartFile multipartFile: files) {
            if (multipartFile != null && !multipartFile.isEmpty()) {
                try {
                    String boardPath = myFileUtils.getBoardPath();
                    File dir = new File(boardPath);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }

                    String boardOrigin = multipartFile.getOriginalFilename();
                    String boardRename = myFileUtils.getBoardRename(boardOrigin);
                    File file = new File(boardPath, boardRename);
                    multipartFile.transferTo(file);

                    BoardFileDto boardFile = BoardFileDto.builder()
                            .boardRename(boardRename)
                            .boardOrigin(boardOrigin)
                            .boardPath(boardPath)
                            .boardNo(boardNo)
                            .build();

                    int inserted = boardMapper.insertBoardFile(boardFile);
                    if (inserted == 1) {
                        insertBoardFileCount++;
                    }

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return insertBoardFileCount == files.size();
    }

    @Transactional
    public void toggleBoardLike(int boardNo, int empNo) {
        BoardDto boardDto = boardMapper.getBoardByNo(boardNo);
        if (boardDto == null) {
            throw new IllegalArgumentException("존재하지 않는 게시판입니다.");
        }

        int likeStatus = boardMapper.isLiked(boardNo, empNo);
        if (likeStatus == 0) {
            boardDto.addLike();
            boardMapper.addLike(boardNo, empNo);
        } else {
            boardDto.removeLike();
            boardMapper.removeLike(boardNo, empNo);
        }

        boardMapper.updateLikeCount(boardDto.getBoardLikeCount(), boardNo);
    }

    // 조회수 증가
    @Transactional
    public void increaseBoardHit(int boardNo, HttpServletRequest request, HttpServletResponse response) {
        BoardDto board = boardMapper.getBoardByNo(boardNo);


        Cookie[] cookies = request.getCookies();
        Cookie hitCookie = null;
        boolean isCookieUpdated = false;

        // 쿠키가 존재하는 경우
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("boardHit")) {
                    hitCookie = cookie;
                    // 쿠키의 값에 현재 게시물 번호가 포함이 안 되어 있으면 진입, 있으면 바로 break
                    if (!cookie.getValue().contains("[" + boardNo + "]")) {
                        boardMapper.increaseBoardHit(boardNo); // DB 업데이트
                        board.addBoardHit();
                        cookie.setValue(cookie.getValue() + "[" + boardNo + "]");
                        isCookieUpdated = true;
                    }
                    break;
                }
            }
        }

        // boardHit 쿠키가 존재하지 않는 경우
        if (hitCookie == null) {
            boardMapper.increaseBoardHit(boardNo); // DB 업데이트
            board.addBoardHit();
            hitCookie = new Cookie("boardHit", "[" + boardNo + "]");
            isCookieUpdated = true;
        }

        // 쿠키가 갱신된 경우
        if (isCookieUpdated) {
            int maxAge = getEndOfDay(); // 자정을 기준으로 조회수 쿠키 초기화
            hitCookie.setPath("/");
            hitCookie.setMaxAge(maxAge);
            response.addCookie(hitCookie);
        }

        log.info("Increased board hit for boardNo: {} {}", boardNo, board.getBoardHit());
    }

    // 현재 시간부터 자정까지 남은 초 계산
    private int getEndOfDay() {
        long todayEndSecond = LocalDate.now().atTime(LocalTime.MAX).toEpochSecond(ZoneOffset.UTC);
        long currentSecond = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);

        return (int) (todayEndSecond - currentSecond);
    }

}
