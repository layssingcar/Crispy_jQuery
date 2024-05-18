package com.mcp.crispy.stock.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("crispy")
public class StockController {
	
	/** 재고 현황 조회
	 * 
	 * @return forward (stock-list.html)
	 */
	@GetMapping("stock-list")
	public String stockList() {
		return "stock/stock-list";
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
