package com.example.casshiroclient.controller;

import com.example.casshiroclient.entity.User;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName: cas-shiro-client
 * @Package: com.example.casshiroclient.controller
 * @ClassName: UserController
 * @Author: jibl
 * @Description:
 * @Date: 2021/4/16 13:06
 * @Version: 1.0
 */
@CrossOrigin
@RestController
@RequestMapping("/user")
public class UserController {

    private static List<User> users = new ArrayList<>();

    static {
        users.add(new User("jibl","jibl"));
        users.add(new User("admin","admin"));
    }


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
}
