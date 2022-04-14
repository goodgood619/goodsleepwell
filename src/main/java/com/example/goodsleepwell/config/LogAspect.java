package com.example.goodsleepwell.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LogAspect {
    Logger logger = LoggerFactory.getLogger(LogAspect.class);

    @Around("execution(* com.example.goodsleepwell.controller.UserController.*(..))")
    public Object logging(ProceedingJoinPoint pjp) throws Throwable {
        logger.info("rest api in ok -"+pjp.getSignature().getDeclaringTypeName()+"/"+pjp.getSignature().getName());
        Object ret = pjp.proceed();
        logger.info("rest api out ok -"+pjp.getSignature().getDeclaringTypeName()+"/"+pjp.getSignature().getName());
        return ret;
    }
}
