package com.mcp.crispy.sales.service;

import com.mcp.crispy.sales.dto.SalesDto;
import com.mcp.crispy.sales.mapper.SalesMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class SalesService {

	private final SalesMapper salesMapper;

	/* 매출 COUNT */
	@Transactional(readOnly = true)
	public int getTotalCount(String search) {
		return salesMapper.getTotalCount(search);
	}

	/* 매장 매출 LIST */
	@Transactional(readOnly = true)
	public List<SalesDto> getSalesList(Integer page, int cnt, String search) {
		if (page == null || page < 1) {
			page = 1;
		}

		if (search == null) {
			search = ""; // Set default value if null
		}

		int totalCount = getTotalCount(search);

		int totalPage = (int) Math.ceil((double) totalCount / cnt); // 총 페이지 수 계산

		// 현재 페이지를 벗어나지 않도록 보정
		page = Math.min(page, totalPage);

		int begin = (page - 1) * cnt + 1; // 현재 페이지에 해당하는 게시물의 시작 인덱스
		int end = Math.min(begin + cnt - 1, totalCount); // 현재 페이지에 해당하는 게시물의 끝 인덱스

		Map<String,Object> map = Map.of("begin", begin, "end", end, "search", search);

		return salesMapper.getSalesList(map);
	}

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
	public List<SalesDto> findGuAvgSales(int month){
		return salesMapper.findGuAvgSales(month);
	}

	/* 이달의 매장 순위 */
	public void findSalesRenk(){
		return;
	}

}
