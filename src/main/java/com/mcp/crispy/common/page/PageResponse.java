package com.mcp.crispy.common.page;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.formula.functions.T;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {
    private List<T> items;      // 현재 페이지에 표시할 항목 리스트
    private int totalPage;      // 전체 페이지 수
    private int startPage;      // 페이지 네비게이션 시작 페이지 번호
    private int endPage;        // 페이지 네비게이션 끝 페이지 번호
    private int currentPage;    // 현재 페이지 번호
    private int prevPage;       // 이전 페이지 번호
    private int nextPage;       // 다음 페이지 번호

    public PageResponse(List<T> items, int totalPage, int startPage, int endPage, int currentPage) {
        this.items = items;
        this.totalPage = totalPage;
        this.startPage = startPage;
        this.endPage = endPage;
        this.currentPage = currentPage;
    }
}
