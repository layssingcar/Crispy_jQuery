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
    private String sortKey;         // 정렬기준
    private String sortOrder;       // 정렬순서
    private String stockNameSearch; // 재고명검색
}
