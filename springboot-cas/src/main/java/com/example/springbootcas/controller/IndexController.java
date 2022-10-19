package com.example.springbootcas.controller;

import org.jasig.cas.client.authentication.AttributePrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Map;

@RestController
public class IndexController {

    @RequestMapping("/test")
    public String test(HttpServletRequest request){
        System.out.println("test............");
        Principal principal = request.getUserPrincipal();
        //cas自定义返回数据
        Map<String, Object> attributes = ((AttributePrincipal) principal).getAttributes();
        return "test";
    }

}
