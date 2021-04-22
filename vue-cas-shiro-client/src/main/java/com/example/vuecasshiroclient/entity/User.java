package com.example.vuecasshiroclient.entity;

/**
 * @ProjectName: vue-cas-shiro-client
 * @Package: com.example.vuecasshiroclient.entity
 * @ClassName: User
 * @Author: jibl
 * @Description:
 * @Date: 2021/4/19 11:51
 * @Version: 1.0
 */
public class User {
    private String username;
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
