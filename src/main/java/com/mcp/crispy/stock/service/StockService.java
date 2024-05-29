package com.mcp.crispy.stock.service;

import com.mcp.crispy.stock.dto.StockDto;
import com.mcp.crispy.stock.mapper.StockMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockMapper stockMapper;

    // 재고 현황 조회
    public List<StockDto> getStockList(int frnNo) {
        return stockMapper.getStockList(frnNo);
    }

    // 재고 상세 조회
    public StockDto getStockDetail(int stockNo) {
        return stockMapper.getStockDetail(stockNo);
    }

}
