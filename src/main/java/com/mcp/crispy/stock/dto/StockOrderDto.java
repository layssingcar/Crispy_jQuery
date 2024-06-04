package com.mcp.crispy.stock.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockOrderDto {
    private List<StockDto> stockOrderList;
    private int empNo;              // 직원번호
    private int orderTempNo;        // 발주임시번호
}
