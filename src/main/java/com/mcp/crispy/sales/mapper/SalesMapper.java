package com.mcp.crispy.sales.mapper;

import com.mcp.crispy.sales.dto.SalesDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SalesMapper {

	/* 매장 매출 LIST */
	List<SalesDto> getSalesList(int frnNo);

	/* 매출 INSERT */
	int insertSales(SalesDto salesDto);

	/* 일별 매출 */
	List<SalesDto> findDailySales(int frnNo);


	/* 달별 매출 조회*/
	List<SalesDto> findMonthlySales(int frnNo);

	/* 년별 매출 */
	List<SalesDto> findYearlySales(int frnNo);

	/* 기간별 평균 매출 */
	String findAvgSales();

	/* 구별 매출 조회 : 카테고리, 가맹점 테이블 */
	List<SalesDto> findGuAvgSales(int month);

	/* 이달의 매장 순위 */
	void findSalesRenk();

}
