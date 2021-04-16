package com.example.casshiroclient.entity;

/**
 * @ProjectName: cas-shiro-client
 * @Package: com.example.casshiroclient.entity
 * @ClassName: User
 * @Author: jibl
 * @Description:
 * @Date: 2021/4/15 15:40
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

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
