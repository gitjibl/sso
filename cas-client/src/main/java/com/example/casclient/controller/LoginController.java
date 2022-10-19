package com.example.casclient.controller;

import org.jasig.cas.client.authentication.AttributePrincipal;
import org.jasig.cas.client.validation.Assertion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@Controller
public class LoginController {
    @Autowired
    private HttpServletRequest request;

    @RequestMapping({"/getUserInfo"})
    @ResponseBody
    public Object getUserInfo() {
        Map<String, Object> userApi = new HashMap();

        Assertion assertion = (Assertion) this.request.getSession().getAttribute("_const_cas_assertion_");
        if (assertion != null) {
            String loginName = assertion.getPrincipal().getName();

            Principal principal = (AttributePrincipal) this.request.getUserPrincipal();
            String id = this.request.getSession().getId();
            System.out.println("JSESSIONID:  "+id);
            if ((this.request.getUserPrincipal() != null) &&
                    ((principal instanceof AttributePrincipal))) {
                Map<String, Object> result = ((AttributePrincipal) principal).getAttributes();
                userApi.put("loginName", loginName);
                userApi.put("sex", result.get("sex"));
                userApi.put("userName", result.get("userName"));
                userApi.put("telephone", result.get("telephone"));
                userApi.put("remark", result.get("remark"));
                userApi.put("JSESSIONID",id);
            }
        }
        return userApi;
    }

    @RequestMapping(value = {"/redirect"}, produces = {"text/plain;charset=UTF-8"})
    public String redirect(String url,HttpSession session) {
        String sessionId = session.getId();
        return "redirect:" + url;
    }



    @RequestMapping(value = {"/test"})
    @ResponseBody
    public String test(HttpSession session) {
        System.out.println("test......");
        return "test";
    }
}