package com.example.vuecasshiroclient.controller;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ProjectName: vue-cas-shiro-client
 * @Package: com.example.vuecasshiroclient.controller
 * @ClassName: UserController
 * @Author: jibl
 * @Description:
 * @Date: 2021/4/19 13:42
 * @Version: 1.0
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @RequestMapping("/getUserInfos")
    @RequiresRoles("admin")
    @RequiresPermissions("admin:add")
    public String getUserInfos(){
        return "访问成功";
    }

    @RequestMapping("/getTestInfos")
    @RequiresRoles("test")
    @RequiresPermissions("test:add")
    public String getTestInfos(){
        return "访问成功";
    }

    @RequestMapping("/test")
    public String test(){
        return "访问成功";
    }
}
