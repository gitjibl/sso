package com.example.demo.controller;

import org.jasig.cas.client.authentication.AttributePrincipal;
import org.jasig.cas.client.util.AbstractCasFilter;
import org.jasig.cas.client.validation.Assertion;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @ProjectName: springboot-cas
 * @Package: com.example.demo.controller
 * @ClassName: TestController
 * @Author: jibl
 * @Description:
 * @Date: 2022/6/8 14:21
 * @Version: 1.0
 */
@Controller
public class TestController {
    @Value(value = "${cas.server-url-prefix}")
    private String serverUrlPrefix;

    @Value(value = "${cas.client-host-url}")
    private String clientHostUrl;

    @RequestMapping("/login")
    @ResponseBody
    public String login(HttpServletRequest request) {
        Assertion assertion = (Assertion) request.getSession().getAttribute(AbstractCasFilter.CONST_CAS_ASSERTION);
        String loginName = null;
        if (assertion != null) {
            AttributePrincipal principal = assertion.getPrincipal();
            loginName = principal.getName();
            System.out.println("访问者：" + loginName);
        }
        return "success";
    }

    @RequestMapping("/logout")
    public String logout(HttpServletRequest request) {
        request.getSession().invalidate();
        return "redirect:" + serverUrlPrefix + "/logout?service=" + clientHostUrl;
    }

    @RequestMapping("/test")
    @ResponseBody
    public String test(HttpServletRequest request) {
        return "test";
    }
}
