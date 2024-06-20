package com.mcp.crispy.approval.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApprFileDto {
    private int apprFileNo;     // 첨부파일번호
    private String apprOrigin;  // 첨부파일원본명
    private String apprRename;  // 첨부파일변경명
    private String apprPath;    // 첨부파일경로
    private int apprNo;         // 문서번호
    private MultipartFile apprFile;
}