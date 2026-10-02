package com.togedy.togedy_server_v2.global.logging;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Aspect
@Component
public class ControllerLoggingAspect {

    private static final long DEFAULT_SLOW_THRESHOLD_MILLIS = 2000;

    @Around("@within(org.springframework.web.bind.annotation.RestController)")
    public Object logRequest(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        boolean failed = false;

        try {
            return joinPoint.proceed();
        } catch (Throwable e) {
            failed = true;
            throw e;
        } finally {
            long elapsed = System.currentTimeMillis() - start;
            log(joinPoint, elapsed, failed);
        }
    }

    private void log(ProceedingJoinPoint joinPoint, long elapsed, boolean failed) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String handler = signature.getDeclaringType().getSimpleName() + "." + signature.getName();
        String request = resolveRequest();
        String result = failed ? "FAIL" : "OK";

        long threshold = resolveThreshold(signature);
        if (elapsed >= threshold) {
            log.warn("[SLOW] {} {} {} {}ms (threshold={}ms)", request, handler, result, elapsed, threshold);
            return;
        }
        log.info("{} {} {} {}ms", request, handler, result, elapsed);
    }

    private String resolveRequest() {
        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes) {
            HttpServletRequest request = attributes.getRequest();
            return request.getMethod() + " " + request.getRequestURI();
        }
        return "-";
    }

    private long resolveThreshold(MethodSignature signature) {
        SlowThreshold slowThreshold = signature.getMethod().getAnnotation(SlowThreshold.class);
        if (slowThreshold != null) {
            return slowThreshold.millis();
        }
        return DEFAULT_SLOW_THRESHOLD_MILLIS;
    }
}
