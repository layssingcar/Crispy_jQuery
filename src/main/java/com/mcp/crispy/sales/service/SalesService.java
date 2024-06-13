package com.mcp.crispy.sales.service;

import com.mcp.crispy.sales.dto.SalesDto;
import com.mcp.crispy.sales.mapper.SalesMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SalesService {

	private final SalesMapper salesMapper;

	

	/* 매출 INSERT */
	@Transactional
	public int insertSales(SalesDto salesDto){
		log.info("insertSales: {}", salesDto.getFrnNo());
		return salesMapper.insertSales(salesDto);
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

	/* 구별 매출 조회 : 카테고리, 가맹점 테이블 */
	public void findGuAvgSales(){
		return;
	}

	/* 이달의 매장 순위 */
	public void findSalesRenk(){
		return;
	}

	/* 가맹점별 매출 */
	public List<SalesDto> getSalesList(int frnNo) {
		return salesMapper.getSalesList(frnNo);
	}
}
