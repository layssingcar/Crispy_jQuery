package com.mcp.crispy.sales.controller;

import com.mcp.crispy.auth.domain.EmployeePrincipal;
import com.mcp.crispy.sales.dto.SalesDto;
import com.mcp.crispy.sales.service.SalesService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller
@RequiredArgsConstructor
@RequestMapping("/crispy")
public class SalesController {

	private final SalesService salesService;
	
	/* 매장 매출 목록 */
	@GetMapping("/salesCalender")
	public String sales(Model model, @RequestParam(value = "page", required = false) Integer page
			                       , @RequestParam(value = "search", required = false) String search
	 							   , Authentication authentication) {
		// 로그인한 정보
		EmployeePrincipal principal = (EmployeePrincipal) authentication.getPrincipal();
		model.addAttribute("emp", principal.getEmployee());

		if(page == null) {
			page = 1;
		}
		// 가맹점 매출 목록
		List<SalesDto> salesList = salesService.getSalesList(page, 10, search);
		model.addAttribute("salesList", salesList);

		// 매출 전체 목록 조회
		for(SalesDto b : salesList)
			System.out.println(b);

		// 전체 게시물 수 조회
		int totalCount = salesService.getTotalCount(search);
		model.addAttribute("totalCount", totalCount);

		// 전체 게시물 / 10
		int maxPage = (int)Math.ceil((double)totalCount/10);
		int pageShow = 10;
		int startPage = ((page - 1) / pageShow) * pageShow + 1;
		int endPage = startPage + pageShow - 1;

		// 다음 페이지, 이전 페이지 계산
		int nextPage = Math.min(page + 10, maxPage);
		int prevPage = Math.max(page - 10, 1);

		model.addAttribute("nextPage", nextPage);
		model.addAttribute("prevPage", prevPage);

		// 시작번호, 끝번호 계산 후 표출
		endPage = Math.min(endPage, maxPage);
		startPage = Math.max(startPage, 1);

		model.addAttribute("currentPage", page);
		model.addAttribute("maxPage", maxPage);
		model.addAttribute("startPage",startPage);
		model.addAttribute("endPage",endPage);

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
		model.addAttribute("frnNo", principal.getFrnNo());

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
