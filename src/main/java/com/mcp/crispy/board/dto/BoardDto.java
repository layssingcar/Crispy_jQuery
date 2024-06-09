package com.mcp.crispy.board.dto;

import com.mcp.crispy.employee.dto.EmployeeDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardDto {
    private int boardNo;
    private int boardCtNo;
    private String boardTitle;
    private String boardContent;
    private int boardHit;
    private Date createDt; ;
    private int creator;
    private Date modifyDt;
    private int modifier;
    private int empNo;
    private int boardLikeCount;
    private String empName;
    private EmployeeDto employee;
    /* 파일 이름*/
    private List<BoardFileDto> files;

    private boolean isLiked;
    private int pageNo;

    // 좋아요 증가
    public void addLike() {
        this.boardLikeCount++;
    }

    // 좋아요 취소
    public void removeLike() {
        this.boardLikeCount--;
    }
}