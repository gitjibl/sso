package com.example.vuecasshiroclient.controller;

import com.example.vuecasshiroclient.constant.CommonConstant;
import com.example.vuecasshiroclient.entity.JwtToken;
import com.example.vuecasshiroclient.util.JwtUtil;
import io.buji.pac4j.subject.Pac4jPrincipal;
import org.pac4j.core.profile.CommonProfile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
public class LoginController {

    /**
     * 登录认证
     * @return 登录结果
     */
    @GetMapping({"/", "", "/index"})
    public void login(HttpServletRequest request, HttpServletResponse response) throws ClassCastException {
        Pac4jPrincipal principal = (Pac4jPrincipal)request.getUserPrincipal();
//        String userId = (String)principal.getProfile().getAttribute("uid");
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
        ((HttpServletResponse) response).setHeader(CommonConstant.ACCESS_TOKEN, token);
        // response.sendRedirect("http://192.168.0.41:8088/#/stats/casindex?token=" + token);
//        try {
//            response.sendRedirect("http://192.168.0.41:8088/#/stats/casindex");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

}
