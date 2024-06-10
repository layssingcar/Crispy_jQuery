package com.mcp.crispy.sales.mapper;

import java.util.List;

import com.mcp.crispy.sales.dto.SalesDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SalesMapper {

	/* 매출입력 */
	void insertSales();

	/* 일별 매출 */
	void findDailySales();

	/* 주간 매출 조회*/
	void findWeeklySales();

	/* 달별 매출 조회*/
	void findMonthlySales();

	/* 년별 매출 */
	void findYearlySales();

	/* 기간별 평균 매출 */
	void findAvgSales();

	/* 구별 매출 조회 : 카테고리, 가맹점 테이블 */
	void findGuAvgSales();

	/* 이달의 매장 순위 */
	void findSalesRenk();
	
	/* 매장 매출 목록 */
	List<SalesDto> getSalesList();
}
