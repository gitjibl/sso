package com.example.vuecasshiroclient.controller;

import com.example.vuecasshiroclient.constant.CommonConstant;
import com.example.vuecasshiroclient.entity.JwtToken;
import com.example.vuecasshiroclient.util.JwtUtil;
import io.buji.pac4j.subject.Pac4jPrincipal;
import org.jasig.cas.client.util.AbstractCasFilter;
import org.jasig.cas.client.validation.Assertion;
import org.pac4j.core.profile.CommonProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

/**
 * @ProjectName: vue-cas-shiro-client
 * @Package: com.example.vuecasshiroclient.controller
 * @ClassName: LoginController
 * @Author: jibl
 * @Description:
 * @Date: 2021/4/19 13:21
 * @Version: 1.0
 */
@RestController
@CrossOrigin
public class LoginController {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;
    /**
     * 登录认证
     * @return 登录结果
     */
    @GetMapping({"/ssoLogin"})
    public void ssoLogin()  {
        // 将签发的 JWT token 设置到 HttpServletResponse 的 Header中,并重写向vue前端页面
        //response.setHeader(CommonConstant.ACCESS_TOKEN, token);
    }
    @GetMapping({"/redirect"})
    public void redirect(String url) {
        HttpSession session = request.getSession();
        Assertion assertion = (Assertion) session.getAttribute(AbstractCasFilter.CONST_CAS_ASSERTION);
        Pac4jPrincipal principal = (Pac4jPrincipal)request.getUserPrincipal();
        String userId = (String)principal.getProfile().getAttribute("username");
        List<CommonProfile> profiles = principal.getProfiles();
        Map<String, Object> attributes = profiles.get(0).getAttributes();
        String username = attributes.get("username").toString();
        /*
        根据统一认证返回信息确定用户角色，并一同写入数据库
        略
        */
        //String pwd = userService.getPasswordByUserId(userId);
        String pwd = "jibl";
        String token = JwtUtil.sign(username, pwd);
        // 将签发的 JWT token 设置到 HttpServletResponse 的 Header中,并重写向vue前端页面
        response.setHeader(CommonConstant.ACCESS_TOKEN, token);
        try {
            response.sendRedirect(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
