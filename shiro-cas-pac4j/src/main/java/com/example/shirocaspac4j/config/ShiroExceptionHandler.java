package com.example.shirocaspac4j.config;

import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @ProjectName: shiro-cas-pac4j
 * @Package: com.example.shirocaspac4j.config
 * @ClassName: ShiroExceptionHandler
 * @Author: jibl
 * @Description:
 * @Date: 2021/5/7 14:21
 * @Version: 1.0
 */
@RestControllerAdvice
public class ShiroExceptionHandler {

//    @ExceptionHandler(value = {AuthorizationException.class})
//    public Map<String, Object> authorizationExceptionHandler(HttpServletRequest request, Exception e) {
//        return noPermissionResult();
//    }

    @ExceptionHandler(value = {UnauthorizedException.class})
    public Map<String, Object> unauthorizedExceptionHandler(HttpServletRequest request, Exception e) {
        return noPermissionResult();
    }

    private Map<String, Object> noPermissionResult() {
        Map<String, Object> result = new HashMap<String, Object>() {
            private static final long serialVersionUID = 1L;

            {
                put("errcode", 0);
                put("errmsg", "暂无权限");
            }
        };
        return result;
    }

}
