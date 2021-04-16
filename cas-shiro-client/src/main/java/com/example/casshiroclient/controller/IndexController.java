package com.example.casshiroclient.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.Principal;

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

}
