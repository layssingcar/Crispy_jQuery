package com.mcp.crispy.map.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MapDto {

    /* 가맹점 */
    private int frnNo;
    private String frnName;
    private String frnX;
    private String frnY;
    private String frnStreet;
    private String frnDetail;
    private String frnTel;

    /* 재고 */
    private int stockNo;
    private String stockName;

    /* 재고 카테고리 */
    private int stockCtNo;
    private String stockCtName;

    /* 재고현황 수량 */
    private int isCount;

}
