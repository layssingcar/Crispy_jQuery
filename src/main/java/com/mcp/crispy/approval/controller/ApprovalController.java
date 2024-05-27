package com.mcp.crispy.approval.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("crispy")
public class ApprovalController {
	
	/** 휴가 및 휴직 신청 목록 조회
	 * 
	 * @return forward (approval-list.html)
	 */
	@GetMapping("approval-list")
	public String apprList() {
		return "approval/approval-list";
	}
	
	/** 결재 문서 열람
	 * 
	 * @return forward (approval-detail.html)
	 */
	@GetMapping("approval-detail")
	public String apprDetail() {
		return "approval/approval-detail";
	}
	
	/** 휴가, 휴직 신청
	 * 
	 * @return forward (time-off-approval.html)
	 */
	@GetMapping("time-off-approval")
	public String timeOffAppr() {
		return "approval/time-off-approval";
	}
	
	// 결재선 선택 (임시)
	@GetMapping("time-off-approval-2")
	public String timeOffAppr2() {
		return "approval/time-off-approval-2";
	}
	
}
