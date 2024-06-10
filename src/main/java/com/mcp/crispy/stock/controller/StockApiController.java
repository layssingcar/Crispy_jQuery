package com.mcp.crispy.stock.controller;


import com.mcp.crispy.auth.domain.EmployeePrincipal;
import com.mcp.crispy.common.page.PageResponse;
import com.mcp.crispy.stock.dto.StockDto;
import com.mcp.crispy.stock.dto.StockOptionDto;
import com.mcp.crispy.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/api/stock")
@RequiredArgsConstructor
public class StockApiController {

    private final StockService stockService;

    // 재고 현황 조회
    @GetMapping("/stockList/v1")
    public ResponseEntity<?> stockList(StockOptionDto stockOptionDto,
                                       Authentication authentication) {
        EmployeePrincipal userDetails = (EmployeePrincipal) authentication.getPrincipal();
        stockOptionDto.setFrnNo(userDetails.getFrnNo());

        PageResponse<StockDto> stockList = stockService.getStockList(stockOptionDto, 20);
        return ResponseEntity.ok(stockList);
    }

    @GetMapping("/categories/v1")
    public ResponseEntity<?> stockCategory() {
        List<StockDto> stockCtList = stockService.getStockCtList();
        return ResponseEntity.ok(stockCtList);
    }

}

