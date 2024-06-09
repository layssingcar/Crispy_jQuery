package com.mcp.crispy.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
    @NotBlank(message = "댓글 내용은 필수입니다.")
    @Size(max = 300, message = "댓글은 300자 이내여야합니다.")
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
