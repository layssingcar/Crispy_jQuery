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
	
	/* 매장 매출 목록 */
	@GetMapping("/sales")
	public String sales(Model model, Authentication authentication) {
		// 로그인한 정보
		EmployeePrincipal principal = (EmployeePrincipal) authentication.getPrincipal();
		model.addAttribute("emp", principal.getEmployee());
		model.addAttribute("frnNo", principal.getFrnNo());

		// 가맹점 매출 목록
		List<SalesDto> salesList = salesService.getSalesList(principal.getFrnNo());
		log.info("salesList {}", salesList);
		model.addAttribute("salesList", salesList);

		return "sales/sales";
	}

	/* 매출입력 */
	@PostMapping("/salesInsert")
	public String insertSales(@RequestBody SalesDto salesDto
			                             , Authentication authentication
								         , Model model) {
		int salesInsert = salesService.insertSales(salesDto);

		/* 로그인 정보 */
		EmployeePrincipal principal = (EmployeePrincipal) authentication.getPrincipal();
		model.addAttribute("emp", principal.getEmployee());

		if (salesInsert > 0) {
			return "redirect:/crispy/salesCalender";
		} else {
			return "Error";
		}
	}

	/* 일별 매출 */
	public void findDailySales(final Model model) {
	}

	/* 주간 매출 조회*/
	public void findWeeklySales(final Model model) {
	}

	/* 달별 매출 조회*/
	public void findMonthlySales(final Model model) {
	}

	/* 년별 매출 */
	public void findYearlySales(final Model model) {

	}

	/* 기간별 평균 매출 */
	public void findAvgSales(final Model model) {
	}

	/* 구별 매출 조회 : 카테고리, 가맹점 테이블 */
	public void findGuAvgSales(final Model model) {
	}

	/* 이달의 매장 순위 */
	public void findSalesRenk(){
	}
}
