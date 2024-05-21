package com.mcp.crispy.attendance.controller;

import com.mcp.crispy.attendance.dto.AttendanceDto;
import com.mcp.crispy.attendance.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/crispy/atten")
public class AttendanceController {
	private final AttendanceService attendanceService;

	@PostMapping(value="/registAtt", produces = "application/json")
	public ResponseEntity<Integer> insertAttendance(@RequestBody AttendanceDto attendanceDto, Principal principal)
	{	
	    attendanceDto.setAttInDt(new Timestamp(attendanceDto.getAttInDt().getTime()));
	    attendanceDto.setAttOutDt(new Timestamp(attendanceDto.getAttOutDt().getTime()));
	    
		int insertCount = attendanceService.insertAttendance(attendanceDto);
		return ResponseEntity.ok(insertCount);
	}
	
	@ResponseBody
	@GetMapping(value = "", produces = "application/json") 	
	public List<AttendanceDto> getAttendenceList(@PathVariable(value = "boardNo") Optional<String> opt, Model model) {
		List<AttendanceDto> list = attendanceService.getAttendenceList(model, 0);
		return list;
	}
}
