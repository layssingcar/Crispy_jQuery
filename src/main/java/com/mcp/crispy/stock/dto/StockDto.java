package com.mcp.crispy.stock.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockDto {
    private int stockNo;            // 재고번호
    private String stockName;       // 재고명
    private String stockImg;        // 재고이미지
    private String stockDetail;     // 재고설명
    private String stockUnit;       // 재고단위
    private int stockPrice;         // 재고단가
    private int stockCtNo;          // 카테고리번호
    private String stockCtName;     // 카테고리명
    private int isCount;            // 재고수량
    private int frnNo;              // 가맹점번호

    private int stockOrderCount;    // 수량
    private int stockOrderCost;     // 금액
}
