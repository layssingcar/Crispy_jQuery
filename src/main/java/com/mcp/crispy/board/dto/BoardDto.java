package com.mcp.crispy.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

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
}