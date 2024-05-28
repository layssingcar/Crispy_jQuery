package com.mcp.crispy.stock.controller;

import com.mcp.crispy.common.userdetails.CustomDetails;
import com.mcp.crispy.employee.dto.EmployeeDto;
import com.mcp.crispy.stock.dto.StockDto;
import com.mcp.crispy.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("crispy")
@RequiredArgsConstructor
@Slf4j
public class StockController {

	private final StockService stockService;

	/**
	 * 재고 현황 조회
	 * 우혜진 (2024. 05. 28.)
	 *
	 * @param authentication
	 * @param model
	 * @return forward (stock-list.html)
	 */
	@GetMapping("stock-list")
	public String stockList(Authentication authentication, Model model) {

		CustomDetails userDetails = (CustomDetails) authentication.getPrincipal();
		int frnNo = userDetails.getFrnNo();

		List<StockDto> stockDtoList = stockService.getStockList(frnNo);
		stockDtoList.forEach(stock -> log.info(stock.toString()));
		model.addAttribute("stockDtoList", stockDtoList);

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
