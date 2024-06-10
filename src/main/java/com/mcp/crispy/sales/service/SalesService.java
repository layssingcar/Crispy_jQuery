package com.mcp.crispy.sales.service;

import java.util.List;

import com.mcp.crispy.sales.dto.SalesDto;
import com.mcp.crispy.sales.mapper.SalesMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SalesService {

	private final SalesMapper salesMapper;

	/* 매출입력 */
	@Transactional
	public void insertSales(){
		return;
	}

	/* 일별 매출 */
	public void findDailySales(){
		return;
	}

	/* 주간 매출 조회*/
	public void findWeeklySales(){
		return;
	}

	/* 달별 매출 조회*/
	public void findMonthlySales(){
		return;
	}

	/* 년별 매출 */
	public void findYearlySales(){
		return;
	}

	/* 기간별 평균 매출 */
	public void findAvgSales(){
		return;
	}

//	/* 구별 매출 조회 : 카테고리, 가맹점 테이블 */
	public void findGuAvgSales(){
		return;
	}

	/* 이달의 매장 순위 */
	public void findSalesRenk(){
		return;
	}
	
	/* 매장 매출 목록 */
	public List<SalesDto> getSalesList() {
		return salesMapper.getSalesList();
	}
}
