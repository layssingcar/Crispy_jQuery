package com.mcp.crispy.stock.mapper;

import com.mcp.crispy.approval.dto.ApprLineDto;
import com.mcp.crispy.stock.dto.StockDto;
import com.mcp.crispy.stock.dto.StockOptionDto;
import com.mcp.crispy.approval.dto.ApprovalDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

@Mapper
public interface StockMapper {

    // 전체 재고 항목 수 조회
    int getStockCount(StockOptionDto stockOptionDto);

    // 재고 현황 조회
    List<StockDto> getStockList(StockOptionDto stockOptionDto, RowBounds rowBounds);

    // 재고 카테고리 목록 조회
    List<StockDto> getStockCtList();

    // 재고 상세 조회
    StockDto getStockDetail(int stockNo);

    // 발주 재고 목록 조회
    List<StockDto> getSelectStock(List<Integer> stockNoList);

    // 임시저장 값 존재 여부 확인
    int checkOrderTemp(int empNo);

    // 이전 임시저장 내용 삭제
    int deleteOrderTemp(int empNo);
    
    // 발주 재고 임시저장
    int insertOrderTemp(ApprovalDto approvalDto);

    // 임시저장 내용 불러오기
    List<StockDto> getOrderTemp(int empNo);

    // 발주 신청 (전자결재 테이블)
    int insertApproval(ApprovalDto approvalDto);

    // 발주 신청 (발주신청서 테이블)
    int insertOrder(ApprovalDto approvalDto);

    // 발주 신청 (발주재고 테이블)
    int insertStockOrder(ApprovalDto approvalDto);

    // 발주 신청 (결재선 테이블)
    int insertApprLine(ApprovalDto approvalDto);

}
