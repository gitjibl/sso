package com.example.casclient.controller;

import com.example.casclient.config.LoginInterceptor;
import org.apache.tomcat.util.descriptor.web.LoginConfig;
import org.jasig.cas.client.util.AbstractCasFilter;
import org.jasig.cas.client.validation.Assertion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * @ProjectName: cas-client
 * @Package: com.example.casclient.controller
 * @ClassName: IndexController
 * @Author: jibl
 * @Description:
 * @Date: 2021/4/19 17:37
 * @Version: 1.0
 */
@RestController
@CrossOrigin()
@RequestMapping("/index")
public class IndexController {

    @Autowired
    private HttpServletRequest request;

    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public String info() {
        HttpSession session = request.getSession();
        if (session != null) {
            String value = (String) session.getAttribute(LoginInterceptor.SESSION_LOGIN);
            return value;
        }
        return null;
    }

    @RequestMapping(value = "/caslogin", method = RequestMethod.GET)
    public void caslogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String url =  request.getParameter("url");
        HttpSession session = request.getSession();
        Assertion assertion = (Assertion) session.getAttribute(AbstractCasFilter.CONST_CAS_ASSERTION);
        if (assertion != null) {
            //获取登录用户名
            String username = assertion.getPrincipal().getName();
            System.out.println("cas user ---------> " + username);
            if (username != null) {
                session.setAttribute(LoginInterceptor.SESSION_LOGIN, session.getId());
                String jsessionid = session.getId();
                System.out.println("jsessionid ------> " + jsessionid);
                // jsessionid 写入返回头中
                ((HttpServletResponse) response).setHeader("jsessionid", jsessionid);
                // 使用url传递参数,跳转到前端
                response.sendRedirect(url+"?jsessionid=" + jsessionid);
                // 使用nginx代理,跳转到前端
//                response.sendRedirect("http://nginx.anumbrella.net:81/home");
            }
        }
    }
}
