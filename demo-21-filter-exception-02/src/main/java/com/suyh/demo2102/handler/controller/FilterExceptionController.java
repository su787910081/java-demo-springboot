package com.suyh.demo2102.handler.controller;

import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 对filter 或者 interceptor 的异常的拦截处理
 * 刚测试了一下，当该controller 存在，且@RestControllerAdvice 实现的类也存在，则filter 里面的异常将会走此异常处理类
 * 而mvc 的异常，将会按正常的@RestControllerAdvice 的实现类处理。
 * 如果没有实现@RestControllerAdvice 的实现类，则异常全部都会走这里。
 */
@Controller
public class FilterExceptionController extends BasicErrorController {

    public FilterExceptionController() {
        super(new DefaultErrorAttributes(), new ErrorProperties());
    }

    @Override
    @RequestMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
        /*
         * body 中的几个基础参数
         *
         * {
         * "timestamp": "2021-04-11T15:49:59.854+0000",
         * "status": 200,
         * "error": "Internal Server Error",
         * "message": "suyh exception message",
         * "path": "/filter/req/runtime/exception"
         * }
         */
        Map<String, Object> body = getErrorAttributes(request, isIncludeStackTrace(request, MediaType.ALL));
        body.put("status", HttpStatus.OK.value());
        return new ResponseEntity<>(body, HttpStatus.OK);
    }
}
