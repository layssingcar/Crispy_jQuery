package com.mcp.crispy.calendar.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mcp.crispy.calendar.dto.CalendarDto;
import com.mcp.crispy.calendar.service.CalendarService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/detail")
public class CalendarController {
	private final CalendarService calendarService;

	@PostMapping(value="/registCmt", produces = "application/json")
	public ResponseEntity<Integer> insertCalendar()
	{	
		return ResponseEntity.ok(0);
	}
	
	@ResponseBody
	@GetMapping(value = "/getCmt/{boardNo}", produces = "application/json") 	// 전체 댓글 가져오기
	public List<CalendarDto> getCalendarList(@PathVariable(value = "boardNo") Optional<String> opt, Model model) {
		List<CalendarDto> list = calendarService.getCalendarList(model, 0);
		return list;
	}
	
	@ResponseBody
	@DeleteMapping(value = "/deleteCmt/{commentNo}", produces = "application/json") // 삭제할때 쓰는거
	public int deleteCalendar(@PathVariable(value = "commentNo") Optional<String> opt) {
		return 0;
	}
	
	@PostMapping(value="/modifyCmt", produces = "application/json")
	public ResponseEntity<Integer> modifyCalendar()
	{	
		return ResponseEntity.ok(0);
	}
}
