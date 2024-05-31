package com.mcp.crispy.stock.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockOptionDto {
    private int frnNo;      // 가맹점번호
    private int page;       // 페이지번호
    private int stockCtNo;  // 카테고리번호
}
