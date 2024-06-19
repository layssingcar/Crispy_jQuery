package com.mcp.crispy.board.dto;

import com.mcp.crispy.employee.dto.EmployeeDto;
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
    private int boardHit = 0;
    private Date createDt; ;
    private int creator;
    private Date modifyDt;
    private int modifier;
    private int empNo;
    private String empName;
    private EmployeeDto employee;

    /* 파일 이름*/
    private String boardOrigin;
}