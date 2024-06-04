package com.mcp.crispy.common.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Around("execution(* com.mcp.crispy..*.*(..)) && !within(com.mcp.crispy.common.filter.JwtAuthorizationFilter)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();

        String simpleClassName = className.substring(className.lastIndexOf('.') + 1);

        log.info("메소드 시작 : {}.{}", simpleClassName, methodName);
        // 메소드 실행 전 시간 측정
        long start = System.currentTimeMillis();

        try {
            // 대상 메소드 실행
            return joinPoint.proceed();
        } catch(Throwable ex) {
        	log.info("예외 발생 : {}.{} : {}", simpleClassName, methodName, ex.getMessage());
        	throw ex;
        }

        finally {
            // 메소드 실행 후 시간 측정 및 실행 시간 계산
            long executionTime = System.currentTimeMillis() - start;
            log.info("메소드 종료 : {}.{} 실행 시간: {} ms",simpleClassName, methodName ,executionTime);
        }
    }
}
