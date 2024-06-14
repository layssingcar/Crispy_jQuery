package com.mcp.crispy.common.utils;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.mcp.crispy.annual.service.AnnualService;
import com.mcp.crispy.schedule.service.ScheduleService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ScheduleUtil {
	private final ScheduleService scheduleService;
	private final AnnualService annualService;
	
//	@Scheduled(fixedRate = 5000)
	@Scheduled(cron = "0 0 0 * * *")
	public void deleteExpirationScheduleandAnnual() {
		System.out.println("scheduleTest");
	}

}
