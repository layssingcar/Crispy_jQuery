package com.mcp.crispy.annual.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mcp.crispy.annual.dto.AnnualDto;
import com.mcp.crispy.annual.service.AnnualService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/crispy")
public class AnnualController {
	private final AnnualService annualService;
	
	@PostMapping(value="/registAnn", produces = "application/json")
	public ResponseEntity<Integer> insertAnnual(@RequestBody AnnualDto annualDto, Principal principal)
	{	
		int insertCount = annualService.insertAnnual(annualDto);
		return ResponseEntity.ok(insertCount);
	}
	
	@ResponseBody
	@GetMapping(value="/getAnnList", produces="application/json")
	public List<AnnualDto> getAnnList() {
		return annualService.getAnnList();
	}
	
	@ResponseBody
	@GetMapping(value = "/getAnnById", produces = "application/json")
	public AnnualDto getAnnById(@RequestParam("annId") String annId, Model model) {
		return annualService.getAnnById(annId);
	}
	
	@PostMapping(value="/moidfyAnn", produces = "application/json")
	public ResponseEntity<Integer> modifyAnnual(@RequestBody AnnualDto annualDto, Principal principal)
	{	
		int modifyCount = annualService.modifyAnnual(annualDto);
		return ResponseEntity.ok(modifyCount);
	}
	@PostMapping(value="/deleteAnn", produces = "application/json")
	public ResponseEntity<Integer> deleteAnnual(@RequestBody AnnualDto annualDto, Principal principal)
	{	
		int deleteCount = annualService.deleteAnnual(annualDto);
		return ResponseEntity.ok(deleteCount);
	}
	
//	@PostMapping(value="/completeDeleteAnn", produces = "application/json")
//	public ResponseEntity<Integer> completeDeleteAnnual(@RequestBody int scheduleId, Principal principal)
//	{	
//		int deleteCount = annualService.completeDeleteAnnual(annualDto);
//		return ResponseEntity.ok(deleteCount);
//	}
}
