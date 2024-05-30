package com.mcp.crispy.stock.mapper;

import com.mcp.crispy.stock.dto.StockDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Mapper
public interface StockMapper {

    // 재고 현황 조회
    List<StockDto> getStockList(int frnNo, RowBounds rowBounds);

    // 전체 재고 항목 수 조회
    int getStockCount(int frnNo);

    // 재고 상세 조회
    StockDto getStockDetail(int stockNo);

}
