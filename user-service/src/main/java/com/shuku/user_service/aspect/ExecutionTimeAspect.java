package com.shuku.user_service.aspect;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Aspect
@Component
@Slf4j
@Order(1)
public class ExecutionTimeAspect {

    @Pointcut("execution(* com.shuku.user_service.controller.*.*(..))")
    public void controllerMethods(){}


    @Around("controllerMethods()")
    public Object measureExecutionTime(ProceedingJoinPoint pjp) throws Throwable{
        long start = System.nanoTime();
        try{
            return pjp.proceed();
        } finally {
            long end = System.nanoTime();
            long elapsedTime = end - start;
            long elapsedTimeMs = TimeUnit.NANOSECONDS.toMillis(elapsedTime);
            String signature = pjp.getSignature().toShortString();
            log.info("Controller Method {} executed in {}ms", signature,elapsedTimeMs);

        }
    }
}
