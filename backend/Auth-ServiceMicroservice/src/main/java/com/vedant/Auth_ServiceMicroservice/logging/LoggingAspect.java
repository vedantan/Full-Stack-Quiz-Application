package com.vedant.Auth_ServiceMicroservice.logging;


import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger =
            LoggerFactory.getLogger(LoggingAspect.class);

    // -------- CONTROLLER LOGGING --------
    @Before("execution(* com.vedant.Auth_ServiceMicroservice.Controller..*(..))")
    public void logControllerCall(JoinPoint joinPoint){

        logger.info("Controller Method Called: {}",
                joinPoint.getSignature().toShortString());

    }

    // -------- SERVICE LOGGING --------
    @AfterReturning(
            pointcut = "execution(* com.vedant.Auth_ServiceMicroservice.service..*(..))",
            returning = "result"
    )
    public void logServiceResponse(JoinPoint joinPoint, Object result){

        logger.info("Service Method Executed: {}",
                joinPoint.getSignature().toShortString());

        logger.info("Returned Value: {}", result);

    }

    // -------- UTILS LOGGING (JWT GENERATION etc.) --------
    @Before("execution(* com.vedant.Auth_ServiceMicroservice.Utils..*(..))")
    public void logUtilsCall(JoinPoint joinPoint){

        logger.info("Utils Method Called: {}",
                joinPoint.getSignature().toShortString());

    }

    // -------- EXCEPTION LOGGING --------
    @AfterThrowing(
            pointcut = "execution(* com.vedant.Auth_ServiceMicroservice..*(..))",
            throwing = "exception"
    )
    public void logException(JoinPoint joinPoint, Throwable exception){

        logger.error("Exception in Method: {}",
                joinPoint.getSignature().toShortString());

        logger.error("Error Message: {}", exception.getMessage());

    }

}
