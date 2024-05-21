package com.mcp.crispy.attendence.controller;

import java.security.Principal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mcp.crispy.attendence.dto.AttendenceDto;
import com.mcp.crispy.attendence.service.AttendenceService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/atten")
public class AttendenceController {
	private final AttendenceService attendenceService;

	@PostMapping(value="/registAtt", produces = "application/json")
	public ResponseEntity<Integer> insertAttendence(@RequestBody AttendenceDto attendenceDto, Principal principal)
	{	
	    attendenceDto.setAttInDt(new Timestamp(attendenceDto.getAttInDt().getTime()));
	    attendenceDto.setAttOutDt(new Timestamp(attendenceDto.getAttOutDt().getTime()));
	    
		int insertCount = attendenceService.insertAttendence(attendenceDto);
		return ResponseEntity.ok(insertCount);
	}
	
	@ResponseBody
	@GetMapping(value = "", produces = "application/json") 	
	public List<AttendenceDto> getAttendenceList(@PathVariable(value = "boardNo") Optional<String> opt, Model model) {
		List<AttendenceDto> list = attendenceService.getAttendenceList(model, 0);
		return list;
	}
}
