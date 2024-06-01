package com.mcp.crispy.stock.service;

import com.mcp.crispy.common.page.PageResponse;
import com.mcp.crispy.stock.dto.StockDto;
import com.mcp.crispy.stock.dto.StockOptionDto;
import com.mcp.crispy.stock.mapper.StockMapper;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockMapper stockMapper;

    // 재고 현황 조회
    public PageResponse<StockDto> getStockList(StockOptionDto stockOptionDto, int limit) {

        // 현재 페이지 번호
        int page = Math.max(stockOptionDto.getPageNo(), 1);

        // 전체 재고 항목 수
        int totalCount = stockMapper.getStockCount(stockOptionDto);

        // 전체 페이지 수
        int totalPage = totalCount / limit + ((totalCount % limit > 0) ? 1 : 0);

        // 페이지 내비게이션 범위
        int startPage = Math.max(page - 2, 1);
        int endPage = Math.min(page + 2,  totalPage);

        /*
        * 조회 범위
        *  - offset: 조회를 시작할 행의 인덱스
        *  - limit: 조회할 행의 개수
        */
        RowBounds rowBounds = new RowBounds(limit * (page - 1), limit);

        // 재고 항목 리스트
        List<StockDto> items = stockMapper.getStockList(stockOptionDto, rowBounds);

        // PageResponse 객체
        return new PageResponse<>(items, totalPage, startPage, endPage, page);

    }

    // 재고 카테고리 목록 조회
    public List<StockDto> getStockCtList() {
        return stockMapper.getStockCtList();
    }

    // 재고 상세 조회
    public StockDto getStockDetail(int stockNo) {
        return stockMapper.getStockDetail(stockNo);
    }

}
