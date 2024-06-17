package com.mcp.crispy.annual.controller;

import com.mcp.crispy.annual.dto.AnnualDto;
import com.mcp.crispy.annual.service.AnnualService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

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
	public List<AnnualDto> getAnnList(@RequestParam("empNo") int empNo) {
		return annualService.getAnnList(empNo);
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
	
	
	@ResponseBody
	@DeleteMapping(value="/completeDeleteAnn", produces = "application/json")
	public ResponseEntity<Integer> completeDeleteAnn(@RequestBody AnnualDto annualDto, Principal principal){
		int deleteCount = annualService.completeDeleteAnn(annualDto);
		return ResponseEntity.ok(deleteCount);
	}
	
	@PostMapping(value="/revertAnn", produces = "application/json")
	public ResponseEntity<Integer> revertAnnual(@RequestBody AnnualDto annualDto, Principal principal)
	{	
		int revertCount = annualService.revertAnnual(annualDto);
		return ResponseEntity.ok(revertCount);
	}
	
	@ResponseBody
	@GetMapping(value="/getCountAnn", produces = "application/json")
	public int getCountAnn() {
		return annualService.getCountAnn();
	}
}
