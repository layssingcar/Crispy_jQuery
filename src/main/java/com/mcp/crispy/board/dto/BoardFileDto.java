package com.mcp.crispy.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoardFileDto {
    private int boardFileNo;
    private String boardOrigin;
    private String boardRename;
    private String boardPath;
    private int boardNo;
}
