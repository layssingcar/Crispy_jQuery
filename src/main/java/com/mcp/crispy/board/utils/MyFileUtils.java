package com.mcp.crispy.board.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MyFileUtils {
/*    @Value("${file.board-dir.sign}")*/
    public String UP_DIR;


    // 현재 날짜
    public static final LocalDate TODAY = LocalDate.now();

    // 업로드 경로 반환
    public String getBoardPath() {
        return UP_DIR+"board" + DateTimeFormatter.ofPattern("/yyyy/MM/dd").format(TODAY);
    }

    // 저장될 파일명 반환
    public String getBoardRename(String boardOrigin) {
        String extName = null;
        if(boardOrigin.endsWith(".tar.gz")) {
            extName = ".tar.gz";
        } else {
            extName = boardOrigin.substring(boardOrigin.lastIndexOf("."));
        }
        return UUID.randomUUID().toString().replace("-", "") + extName;
    }

    // 임시 파일 경로 반환
    public String getTempPath() {
        return "c:/temporary";
    }

    // 임시 파일 이름 반환 (확장자 제외)
    public String getTempFilename() {
        return System.currentTimeMillis() + "";
    }

}