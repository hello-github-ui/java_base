package com.example.exception;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.Map;

// 使用 @ControllerAdvice 方式来实现对 @RequestMapping 的异常拦截 // 缺点很明显，是对于 controller 的建议，只能对controller起作用
@ControllerAdvice
public class ControllerException {

    private static final Logger log = LoggerFactory.getLogger(ControllerException.class);

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Map<String, String> getMsg(HttpServletRequest request, HttpServletResponse response, Exception e){

        log.error("该服务可能出错了");
        return Collections.singletonMap("msg", "服务出错");
    }
}
