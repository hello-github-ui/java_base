package com.example.exception;

// 使用 aop 来全局拦截异常，可以作用于你自定义的任何包路径下 // 相比 @ControllerAdvice 有优势

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class AOPGlobalException {

    @Pointcut("execution(* com.example..*.*(..))")
    public void point1(){

    }

    @AfterReturning(pointcut = "point1()")
    public void afterReturn(JoinPoint joinPoint){
        log.error("全局捕获到异常了..............");
        //纪录错误信息
        // todo 想要执行的操作
    }
}
