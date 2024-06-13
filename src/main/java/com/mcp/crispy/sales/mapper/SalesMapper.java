package com.mcp.crispy.sales.mapper;

import com.mcp.crispy.sales.dto.SalesDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SalesMapper {

	/* 매장 매출 LIST */
	List<SalesDto> getSalesList(int frnNo);

	/* 매출 COUNT*/
	int getTotalCount(@Param("search")String search, @Param("frnNo")Integer frnNo);

	/* 매출 INSERT */
	int insertSales(SalesDto salesDto);

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

}
