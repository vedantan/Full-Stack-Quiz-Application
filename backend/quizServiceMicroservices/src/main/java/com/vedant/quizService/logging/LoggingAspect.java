package com.vedant.quizService.logging;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    // Before method execution
    @Before("execution(* com.vedant..controller..*(..))")
    public void logBefore(JoinPoint joinPoint) {

        System.out.println("Method Called: " + joinPoint.getSignature().getName());
        System.out.println("Class: " + joinPoint.getTarget().getClass().getSimpleName());

    }

    // After method execution
    @AfterReturning(pointcut = "execution(* com.vedant..service..*(..))", returning = "result")
    public void logAfter(JoinPoint joinPoint, Object result) {

        System.out.println("Method Executed: " + joinPoint.getSignature().getName());
        System.out.println("Returned: " + result);

    }

    // Exception logging
    @AfterThrowing(pointcut = "execution(* com.vedant..*(..))", throwing = "exception")
    public void logException(JoinPoint joinPoint, Throwable exception) {

        System.out.println("Exception in: " + joinPoint.getSignature().getName());
        System.out.println("Error: " + exception.getMessage());

    }
}