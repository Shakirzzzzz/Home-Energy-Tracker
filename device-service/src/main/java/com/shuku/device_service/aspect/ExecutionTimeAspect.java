package com.shuku.device_service.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
@Order(1)
public class ExecutionTimeAspect {

    @Pointcut("execution(* com.shuku.device_service.controller.*.*(..)")
    public void controllerMethods(){}


    @Around("controllerMethods()")
    public Object measureExecutionTime(ProceedingJoinPoint pjp) throws Throwable{
        long start = System.currentTimeMillis();
        try{
            return pjp.proceed();
        }
        finally {
            long end = System.currentTimeMillis();
            long elapsedTime = end - start;
            String signature = pjp.getSignature().toShortString();
            log.info("Controller Method {} executed in {}ms",signature,elapsedTime);

        }
    }
}
