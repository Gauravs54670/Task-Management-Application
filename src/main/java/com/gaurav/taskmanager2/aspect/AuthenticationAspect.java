package com.gaurav.taskmanager2.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
@Aspect
@Slf4j
@Component
public class AuthenticationAspect {
    // Pointcut: applies to all public methods in TaskController
    @Pointcut("execution(public * com.gaurav.taskmanager2.Controller.TaskController.TaskController.*(..))")
    public void controllerMethods() {}

    @Before("controllerMethods()")
    public void logAuthentication(JoinPoint joinPoint) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null &&  authentication.isAuthenticated()) {
            String username = authentication.getName();
            log.info("Authenticated user '{}' is accessing method: {}", username, joinPoint.getSignature().getName());
        } else
            log.warn("Unauthenticated access attempt to: {}", joinPoint.getSignature().getName());
    }
}
