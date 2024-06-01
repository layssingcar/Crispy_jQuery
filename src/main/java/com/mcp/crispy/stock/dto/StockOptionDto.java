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
    private int frnNo;              // 가맹점번호
    private int pageNo;             // 페이지번호
    private int stockCtNo;          // 카테고리번호
    private int stockNameSort;      // 재고명
    private int isCountSort;        // 재고수량
}
