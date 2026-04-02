package com.project.market_service.common.aop;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j(topic = "ASPECT")
@Aspect
@Component
public class LoggingAspect {

    @Around("execution(* com.project.market_service..*Controller.*(..))")
    public Object logExecution(ProceedingJoinPoint joinPoint) throws Throwable {

        long start = System.currentTimeMillis();
        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        try {
            log.info("[Request] {}.{}", className, methodName);

            Object result = joinPoint.proceed();

            long executionTime = System.currentTimeMillis() - start;
            log.info("[END] {}.{} | Time: {}ms", className, methodName, executionTime);

            return result;
        } catch (Exception e) {
            throw e;
        }
    }
}