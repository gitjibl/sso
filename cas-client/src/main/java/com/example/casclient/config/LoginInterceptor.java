package com.example.casclient.config;

import org.jasig.cas.client.util.AbstractCasFilter;
import org.jasig.cas.client.validation.Assertion;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @ProjectName: cas-client
 * @Package: com.example.casclient.config
 * @ClassName: LoginInterceptor
 * @Author: jibl
 * @Description:
 * @Date: 2021/4/19 17:30
 * @Version: 1.0
 */
//@Configuration
public class LoginInterceptor implements WebMvcConfigurer {

    // 普通登录SESSION
    public final static String SESSION_LOGIN = "SESSION_LOGIN";

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //注册TestInterceptor拦截器
        InterceptorRegistration registration = registry.addInterceptor(new AdminInterceptor());
        registration.addPathPatterns("/**");//所有路径都被拦截
        registration.excludePathPatterns("/index/caslogin/**","/test/sayhello");
    }

    private class AdminInterceptor extends HandlerInterceptorAdapter{

        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
            HttpSession session = request.getSession(false);

            if (session != null) {
                System.out.println("requst path " + request.getServletPath());
                Assertion assertion = (Assertion) session.getAttribute(AbstractCasFilter.CONST_CAS_ASSERTION);
                if (assertion != null) {
                    System.out.println("cas user ---------> " + assertion.getPrincipal().getName());
                }
                String value = (String) session.getAttribute(SESSION_LOGIN);
                System.out.println("security session = null ---------> " + (value == null));

                if (value != null) {
                    return true;
                }
            }
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
    }
}
