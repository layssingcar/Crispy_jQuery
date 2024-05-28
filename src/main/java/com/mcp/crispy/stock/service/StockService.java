package com.mcp.crispy.stock.service;

import com.mcp.crispy.stock.dto.StockDto;
import com.mcp.crispy.stock.mapper.StockMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockMapper stockMapper;

    // 재고 현황 조회
    public List<StockDto> getStockList(int frnNo) {
        return stockMapper.getStockList(frnNo);
    }

}
