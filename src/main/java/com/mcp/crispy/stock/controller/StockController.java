package com.mcp.crispy.stock.controller;

import com.mcp.crispy.common.userdetails.CustomDetails;
import com.mcp.crispy.common.page.PageResponse;
import com.mcp.crispy.stock.dto.StockDto;
import com.mcp.crispy.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("crispy")
@RequiredArgsConstructor
@Slf4j
public class StockController {

	private final StockService stockService;

	/**
	 * 재고 현황 조회
	 * 우혜진 (24. 05. 28.)
	 *
	 * @param authentication
	 * @param page
	 * @param model
	 * @return forward (stock-list.html)
	 */
	@GetMapping("stock-list")
	public String stockList(Authentication authentication,
							@RequestParam(value = "page", defaultValue = "1") int page,
							Model model) {

		CustomDetails userDetails = (CustomDetails) authentication.getPrincipal();
		int frnNo = userDetails.getFrnNo();

		PageResponse<StockDto> stockDtoList = stockService.getStockList(frnNo, page, 10);
		model.addAttribute("stockDtoList", stockDtoList);

		return "/stock/stock-list";

	}

	/**
	 * 재고 항목 조회
	 * 우혜진 (24. 05. 29.)
	 *
	 * @param authentication
	 * @param page
	 * @param model
	 * @return result
	 */
	@GetMapping("stock-items")
	public String stockItems(Authentication authentication,
							 @RequestParam(value = "page", defaultValue = "1") int page,
							 Model model) {

		CustomDetails userDetails = (CustomDetails) authentication.getPrincipal();
		int frnNo = userDetails.getFrnNo();

		PageResponse<StockDto> stockDtoList = stockService.getStockList(frnNo, page, 10);
		model.addAttribute("stockDtoList", stockDtoList);

		return "/stock/stock-list :: stock-list-container";

	}

	/**
	 * 재고 상세 조회
	 * 우혜진 (24. 05. 28.)
	 *
	 * @param stockNo
	 * @return result
	 */
	@GetMapping(value = "stock-detail", produces = "application/json")
	public ResponseEntity<StockDto> getStockDetail(@RequestParam("stockNo") int stockNo) {
		return ResponseEntity.ok(stockService.getStockDetail(stockNo));
	}
	
	/** 발주 신청
	 * 
	 * @return forward (stock-order.html)
	 */
	@GetMapping("stock-order")
	public String stockOrder() {
		return "stock/stock-order";
	}

}
