package com.mcp.crispy.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {

    private int cmtNo;
    private String cmtContent;
    private String empName;
    private Date cmtCreateDt; // 작성일
    private Date createDt; // 생성일
    private int creator;
    private Date modifyDt;
    private int modifier;
    private int boardNo;
    private int cmtStat;
    private Integer parentCmtNo;
    private int level;
}
