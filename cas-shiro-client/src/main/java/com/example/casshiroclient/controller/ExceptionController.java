package com.example.casshiroclient.controller;

import org.apache.shiro.authz.AuthorizationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


/**
 * @ProjectName: cas-shiro-client
 * @Package: com.example.casshiroclient.controller
 * @ClassName: ExceptionController
 * @Author: jibl
 * @Description:
 * @Date: 2021/4/16 17:18
 * @Version: 1.0
 */
@RestControllerAdvice
public class ExceptionController {

    // 捕捉shiro的异常
    @ExceptionHandler(AuthorizationException.class)
    public String shiroExString() {
        return "抱歉，您没有权限访问！";
    }


}