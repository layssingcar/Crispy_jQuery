package com.mcp.crispy.common.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.mcp.crispy.annual.dto.AnnualDto;
import com.mcp.crispy.annual.service.AnnualService;
import com.mcp.crispy.schedule.dto.ScheduleDto;
import com.mcp.crispy.schedule.service.ScheduleService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ScheduleUtil {
	private final ScheduleService scheduleService;
	private final AnnualService annualService;
	
//	@Scheduled(fixedRate = 30000)
//	@Scheduled(cron = "0 0 0 * * *")
	public void deleteExpirationScheduleandAnnual() {
		List<ScheduleDto> scheList = scheduleService.getAllScheList();
		List<AnnualDto> annList = annualService.getAllAnnList();
		
		String dateTimeStr;
		LocalDateTime dateTime;
		DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
		LocalDateTime now = LocalDateTime.now();
		
		for(ScheduleDto sche : scheList) {
	        dateTimeStr = sche.getScheEndTime();
	        dateTime = LocalDateTime.parse(dateTimeStr, formatter);
	        if(dateTime.isBefore(now)) {
	        	sche.setScheStat(1);
	        	scheduleService.deleteSchedule(sche);
	        }
		}
		
		for(AnnualDto ann : annList) {
	        dateTimeStr = ann.getAnnEndTime();
	        dateTime = LocalDateTime.parse(dateTimeStr, formatter);
	        if(dateTime.isBefore(now)) {
	        	ann.setAnnCtNo(3);
	        	annualService.deleteAnnual(ann);
	        }
		}
	}

}
