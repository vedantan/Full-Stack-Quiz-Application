package com.vedant.demo.logging;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger log =
            LoggerFactory.getLogger(LoggingAspect.class);

    // Intercepts all controller methods
    @Around( "execution(* com.vedant.demo.controller..*(..)) || execution(* com.vedant.demo.service..*(..)) || execution(* com.vedant.demo.dao..*(..))")
    public Object logControllerMethods(ProceedingJoinPoint joinPoint) throws Throwable {

        String methodName = joinPoint.getSignature().toShortString();

        log.info("➡️ Entering Method: {}", methodName);

        Object[] args = joinPoint.getArgs();

        for (Object arg : args) {
            log.info("Request Argument: {}", arg);
        }

        long startTime = System.currentTimeMillis();

        Object result = joinPoint.proceed();

        long timeTaken = System.currentTimeMillis() - startTime;

        log.info("⬅️ Exiting Method: {}", methodName);
        log.info("Execution Time: {} ms", timeTaken);
        log.info("Response: {}", result);

        return result;
    }

    @AfterThrowing(
            pointcut = "execution(* com.vedant.demo..*(..))",
            throwing = "ex")
    public void logException(Exception ex) {

        log.error("❌ Exception occurred: {}", ex.getMessage());
    }
}