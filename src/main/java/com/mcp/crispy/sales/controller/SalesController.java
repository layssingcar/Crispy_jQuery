package com.mcp.crispy.sales.controller;

import com.mcp.crispy.auth.domain.EmployeePrincipal;
import com.mcp.crispy.sales.dto.SalesDto;
import com.mcp.crispy.sales.service.SalesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/crispy")
public class SalesController {

	private final SalesService salesService;

	/* 매장 매출 전체목록 */
	@GetMapping("/sales")
	public String sales(Model model, Authentication authentication) {
		EmployeePrincipal principal = (EmployeePrincipal) authentication.getPrincipal();
		model.addAttribute("emp", principal.getEmployee());
		model.addAttribute("frnNo", principal.getFrnNo());
		return "sales/sales";
	}

	@ResponseBody
	@GetMapping("/salesList")
	public List<SalesDto> getSalesList(Authentication authentication, Model model) {
		EmployeePrincipal principal = (EmployeePrincipal) authentication.getPrincipal();
		return salesService.getSalesList(principal.getFrnNo());
	}

	@ResponseBody
	@GetMapping("/dailySalesList")
	public List<SalesDto> getDailySalesList(Authentication authentication) {
		EmployeePrincipal principal = (EmployeePrincipal) authentication.getPrincipal();
		return salesService.findDailySales(principal.getFrnNo());
	}

	/* 매출 입력 */
	@PostMapping("/salesInsert")
	@ResponseBody
	public ResponseEntity<String> insertSales(@RequestBody SalesDto salesDto, Authentication authentication) {
		EmployeePrincipal principal = (EmployeePrincipal) authentication.getPrincipal();
		int salesInsert = salesService.insertSales(salesDto);
		if (salesInsert > 0) {
			return ResponseEntity.ok("매출이 성공적으로 등록되었습니다.");
		} else {
			return ResponseEntity.badRequest().body("매출 등록에 실패하였습니다.");
		}
	}

	/* 월별 조회 */
	@ResponseBody
	@GetMapping("/monthlySalesList")
	public List<SalesDto> getMonthlySalesList(Authentication authentication) {
		EmployeePrincipal principal = (EmployeePrincipal) authentication.getPrincipal();
		return salesService.findMonthlySales(principal.getFrnNo());
	}

	/* 연별 조회 */
	@ResponseBody
	@GetMapping("/yearlySalesList")
	public List<SalesDto> getYearlySalesList(Authentication authentication) {
		EmployeePrincipal principal = (EmployeePrincipal) authentication.getPrincipal();
		return salesService.findYearlySales(principal.getFrnNo());
	}

	/* 구별 매출 조회 : 카테고리, 가맹점 테이블 */
	@ResponseBody
	@GetMapping(value = "/guAvgSales", produces = "application/json")
	public List<SalesDto> findGuAvgSales(@RequestParam("month") int month) {
		return salesService.findGuAvgSales(month);
	}

	/* 이달의 매장 순위 */
	@ResponseBody
	@GetMapping("/salesRank")
	public String findSalesRank() {
		return "Not Implemented";
	}

	@GetMapping("/crispy/salesDetail")
	public ResponseEntity<SalesDto> getSalesDetail(@RequestParam("frnNo") int frnNo) {
		SalesDto salesDetail = salesService.salesDetail(frnNo);
		if (salesDetail != null) {
			return ResponseEntity.ok(salesDetail);
		} else {
			return ResponseEntity.notFound().build();
		}
	}
}
