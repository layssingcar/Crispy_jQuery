package com.mcp.crispy.stock.controller;

import com.mcp.crispy.auth.domain.EmployeePrincipal;
import com.mcp.crispy.common.page.PageResponse;
import com.mcp.crispy.stock.dto.StockDto;
import com.mcp.crispy.stock.dto.StockOptionDto;
import com.mcp.crispy.stock.dto.StockOrderDto;
import com.mcp.crispy.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
	 * @param stockOptionDto
	 * @param model
	 * @return forward (stock-list.html)
	 */
	@GetMapping("stock-list")
	public String stockList(Authentication authentication,
							StockOptionDto stockOptionDto,
							Model model) {

		EmployeePrincipal userDetails = (EmployeePrincipal) authentication.getPrincipal();
		stockOptionDto.setFrnNo(userDetails.getFrnNo());

		PageResponse<StockDto> stockDtoList = stockService.getStockList(stockOptionDto, 10);
		model.addAttribute("stockDtoList", stockDtoList);
		model.addAttribute("stockCtList", stockService.getStockCtList());

		return "/stock/stock-list";

	}

	/**
	 * 재고 항목 조회
	 * 우혜진 (24. 05. 29.)
	 *
	 * @param authentication
	 * @param stockOptionDto
	 * @param model
	 * @return result
	 */
	@GetMapping("stock-items")
	public String stockItems(Authentication authentication,
							 StockOptionDto stockOptionDto,
							 Model model) {

		EmployeePrincipal userDetails = (EmployeePrincipal) authentication.getPrincipal();
		stockOptionDto.setFrnNo(userDetails.getFrnNo());

		PageResponse<StockDto> stockDtoList = stockService.getStockList(stockOptionDto, 10);
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
	
	/**
	 * 발주 신청 페이지
	 * 우혜진 (24. 05. 16.)
	 *
	 * @return forward (stock-order.html)
	 */
	@GetMapping("stock-order")
	public String stockOrder() {
		return "stock/stock-order";
	}

	/**
	 * 발주 재고 목록 조회
	 * 우혜진 (24. 06. 03.)
	 *
	 * @param stockNoList
	 * @param model
	 * @return forward (stock-order.html)
	 */
	@PostMapping("stock-order")
	public String stockOrder(@RequestParam("stockNo") List<Integer> stockNoList,
							 Model model) {

		List<StockDto> stockDtoList = stockService.getStockList(stockNoList);
		model.addAttribute("stockDtoList", stockDtoList);

		return "stock/stock-order";

	}

	/**
	 * 발주 재고 임시저장
	 * 우혜진 (24. 06. 03.)
	 *
	 * @param authentication
	 * @param stockOrderDto
	 * @return redirect (stockOrder())
	 */
	@PostMapping("stock-order-temp")
	public String stockOrderTemp(Authentication authentication,
								 @ModelAttribute StockOrderDto stockOrderDto) {

		EmployeePrincipal userDetails = (EmployeePrincipal) authentication.getPrincipal();
		stockOrderDto.setEmpNo(userDetails.getEmpNo());
		log.info("stockOrderDto: {}", stockOrderDto.toString());
		log.info("empNo: {}", userDetails.getEmpNo());

		int result = stockService.insertOrderTemp(stockOrderDto);

		return "redirect:stock-order";

	}

}
