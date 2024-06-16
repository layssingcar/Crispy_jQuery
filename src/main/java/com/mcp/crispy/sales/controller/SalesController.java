package com.mcp.crispy.sales.controller;

import com.mcp.crispy.auth.domain.EmployeePrincipal;
import com.mcp.crispy.sales.dto.SalesDto;
import com.mcp.crispy.sales.service.SalesService;
import com.mcp.crispy.schedule.dto.ScheduleDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
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

	/*	일별 매출 */
	@GetMapping("/daily")
	public String findDailySales(Model model, Authentication authentication) {
		EmployeePrincipal principal = (EmployeePrincipal) authentication.getPrincipal();
		model.addAttribute("emp", principal.getEmployee());
		model.addAttribute("frnNo", principal.getFrnNo());

		List<SalesDto> salesDailyList = salesService.findDailySales(principal.getFrnNo());
		model.addAttribute("salesDailyList", salesDailyList);

		return "sales/daily";
	}
	
	/* 달별 조회*/
	@GetMapping("/monthly")
	public String findMonthlySales(Model model, Authentication authentication) {
		EmployeePrincipal principal = (EmployeePrincipal) authentication.getPrincipal();
		model.addAttribute("emp", principal.getEmployee());
		model.addAttribute("frnNo", principal.getFrnNo());

		List<SalesDto> salesMonthlyList = salesService.findMonthlySales(principal.getFrnNo());
		model.addAttribute("salesMonthlyList", salesMonthlyList);

		return "sales/monthly";
	}

	/* 달별 조회*/
	@GetMapping("/yearly")
	public String findYearlySales(Model model, Authentication authentication) {
		EmployeePrincipal principal = (EmployeePrincipal) authentication.getPrincipal();
		model.addAttribute("emp", principal.getEmployee());
		model.addAttribute("frnNo", principal.getFrnNo());

		List<SalesDto> yearlySalesList = salesService.findYearlySales(principal.getFrnNo());
		model.addAttribute("yearlySalesList", yearlySalesList);

		return "sales/yearly";
	}

	/* 기간별 평균 매출 */
	@GetMapping
	public void findAvgSales(Model model) {
		String avgSalse = salesService.findAvgSales();
		model.addAttribute("avgSalse", avgSalse);

		String avgSalseDate = avgSalse.toString();


		System.out.println("@@@@@@@@@@@@@@@@ : avgSalse" + avgSalseDate);
	}

	/* 구별 매출 조회 : 카테고리, 가맹점 테이블 */
	@ResponseBody
	@GetMapping(value = "/getGuAvgSales", produces = "application/json")
	public List<SalesDto> findGuAvgSales(@RequestParam("month") int month) {
		return salesService.findGuAvgSales(month);
	}

	/* 이달의 매장 순위 */
	public void findSalesRenk(){
	}
}
