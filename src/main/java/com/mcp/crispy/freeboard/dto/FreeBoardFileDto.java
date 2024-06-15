package com.mcp.crispy.freeboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.io.Resource;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FreeBoardFileDto {
    private int boardFileNo;
    private String boardOrigin;
    private String boardRename;
    private String boardPath;
    private int boardNo;
    private Resource resource;
    private long contentLength;

    public FreeBoardFileDto(Resource resource, String boardRename, long contentLength) {
        this.resource = resource;
        this.boardRename = boardRename;
        this.contentLength = contentLength;
    }

}
