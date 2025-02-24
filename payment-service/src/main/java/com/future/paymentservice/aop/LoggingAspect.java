package com.future.paymentservice.aop;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.Objects;

@Aspect
@Component
@Order(1)
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    // Pointcut that matches all controller layers
    @Pointcut("within(com.future.paymentservice.controller.*)")
    public void controllerLayerLogging() {}

    // Pointcut that matches all service layers
    @Pointcut("execution(* com.future.paymentservice.service.*.*(..))")
    public void serviceLayerLogging() {}

    @Before("serviceLayerLogging()")
    public void logBeforeServiceLayer(JoinPoint joinPoint) {
        String name = joinPoint.getSignature().getName();
        logger.info("Method Name: {}, args: {}", name, Arrays.toString(joinPoint.getArgs()));
    }

    // Advice to log a message after methods matching the serviceLayer() pointcut return successfully
    @AfterReturning(pointcut = "serviceLayerLogging()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        String name = joinPoint.getSignature().getName();
        logger.info("Method Name: {}, args: {}, service return: {}", name, Arrays.toString(joinPoint.getArgs()), result);
    }

    // Advice to log a message when a method matching the serviceLayer() pointcut throws an exception
    @AfterThrowing(pointcut = "serviceLayerLogging()", throwing = "ex")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable ex) {
        String name = joinPoint.getSignature().getName();
        logger.error("Exception in {}.{}() with cause = {}", joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(), ex.getCause() != null ? ex.getCause() : "NULL");
    }
    
    @Around("controllerLayerLogging()")
    public Object controllerLayerAround(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取当前请求
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        logger.info("[{}], request method: {}, request url: {}", joinPoint.getSignature().getName(), request.getMethod(), request.getRequestURI());

        // Define start time
        long start = System.currentTimeMillis();

        // Proceeds with the method execution and captures the result
        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable throwable) {
            logger.error("[{}], throw message: {}",
                    joinPoint.getSignature().getName(),
                    throwable.getMessage());
            throw throwable;
        }

        // Define output time
        long end = System.currentTimeMillis();
        logger.info("[{}], response details: {}", joinPoint.getSignature().getName(), convertResponseEntityToString(result));
        logger.info("Execution time of {}: {} ms", joinPoint.getSignature().getName(), end - start);
        // Returns the result of the method execution
        return result;
    }

    // 将 ResponseEntity 转换为字符串
    private String convertResponseEntityToString(Object result) {
        if (result instanceof ResponseEntity<?> responseEntity) {
            return "Status: " + responseEntity.getStatusCode() + "; " +
                    "Headers: " + responseEntity.getHeaders() + "; " +
                    "Body: " + Objects.requireNonNull(responseEntity.getBody()).toString();
        }
        return result != null ? result.toString() : "null";
    }

}
