package com.mcp.crispy.board.dto;

import com.mcp.crispy.employee.dto.EmployeeDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
    @NotBlank(message = "제목은 필수입니다.")
    @Size(max = 100, message = "제목은 100자 이내여야합니다.")
    private String boardTitle;

    @Size(max = 1000, message = "내용은 1000자 이내여야합니다.")
    @NotBlank(message = "내용은 필수입니다.")
    private String boardContent;
    private int boardHit;
    private Date createDt; ;
    private int creator;
    private Date modifyDt;
    private int modifier;
    private int empNo;
    private int boardLikeCount; // 게시물 좋아요 개수
    private String empName;
    private EmployeeDto employee;
    /* 파일 이름*/
    private List<BoardFileDto> files;

    private boolean isLiked; // 좋아요 여부
    private int pageNo; // 페이지번호
    private String sortKey;         // 정렬기준
    private String sortOrder;       // 정렬순서
    private String boardTitleSearch; // 게시물명

    // 좋아요 증가
    public void addLike() {
        this.boardLikeCount++;
    }

    // 좋아요 취소
    public void removeLike() {
        this.boardLikeCount--;
    }

    // 조회수 증가
    public void addBoardHit() {
        this.boardHit+= 1;
    }

}