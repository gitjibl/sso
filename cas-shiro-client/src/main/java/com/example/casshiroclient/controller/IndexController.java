package com.example.casshiroclient.controller;

import io.buji.pac4j.subject.Pac4jPrincipal;
import org.pac4j.core.profile.CommonProfile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Map;

/**
 * @ProjectName: cas-shiro-client
 * @Package: com.example.casshiroclient.controller
 * @ClassName: IndexController
 * @Author: jibl
 * @Description:
 * @Date: 2021/4/16 11:18
 * @Version: 1.0
 */
@Controller
public class IndexController {

    @Value("${cas.server.url}")
    private String casServerUrl;

    @Value("${cas.project.url}")
    private String projectUrl;

    @RequestMapping("/")
    public String index(HttpServletRequest request,Model model) {
        //用户详细信息
        Principal principal = request.getUserPrincipal();
        model.addAttribute("user", principal);
        return "pages/index";
    }

    @RequestMapping("/ssoLogin")
    public void ssoLogin(HttpServletRequest request, HttpServletResponse response) {

    }

    @RequestMapping("/redirect")
    public void redirect(HttpServletRequest request, HttpServletResponse response, String url) throws ClassCastException {
        Pac4jPrincipal principal = (Pac4jPrincipal)request.getUserPrincipal();
        String userId = (String)principal.getProfile().getAttribute("username");
        List<CommonProfile> profiles = principal.getProfiles();
        Map<String, Object> attributes = profiles.get(0).getAttributes();
        String username = attributes.get("username").toString();
        /*
        根据统一认证返回信息确定用户角色，并一同写入数据库
        */
        //String pwd = userService.getPasswordByUserId(userId);
        try {
            response.sendRedirect(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
