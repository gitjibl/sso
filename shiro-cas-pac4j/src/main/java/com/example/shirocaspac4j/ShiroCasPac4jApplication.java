package com.example.shirocaspac4j;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class ShiroCasPac4jApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShiroCasPac4jApplication.class, args);
    }

    @RequiresPermissions(value = "role:edit")
    @GetMapping(value = "/roles/{id}")
    public String put(){
        return "允许修改角色";
    }

    @RequiresPermissions(value = "user:info")
    @GetMapping(value = "/users/{id}")
    public PrincipalCollection getUserById() {
        return SecurityUtils.getSubject().getPrincipals();
    }

    @GetMapping(value = "/you")
    public String you(){
        return "you you 不需要权限";
    }

    @GetMapping(value = "/user/info")
    public String userInfo(){
        return "userInfo";
    }

}
