package com.example.vuecasshiroclient.config;

import io.buji.pac4j.context.ShiroSessionStore;
import org.pac4j.cas.config.CasConfiguration;
import org.pac4j.cas.config.CasProtocol;
import org.pac4j.cas.logout.DefaultCasLogoutHandler;
import org.pac4j.core.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * @ProjectName: vue-cas-shiro-client
 * @Package: com.example.vuecasshiroclient.config
 * @ClassName: Pac4jConfig
 * @Author: jibl
 * @Description:
 * @Date: 2021/4/19 13:03
 * @Version: 1.0
 */
@Configuration
public class Pac4jConfig {
    /**
     * 地址为：cas地址
     */
    @Value("${cas.server.url}")
    private String casServerUrl;

    /**
     * 地址为：验证返回后的项目地址：http://localhost:8081
     */
    @Value("${cas.project.url}")
    private String projectUrl;

    /**
     * 相当于一个标志，可以随意
     */
    @Value("${cas.client-name}")
    private String clientName;



    /**
     * pac4j配置
     *
     * @param casClient
     * @param shiroSessionStore
     * @return
     */
    @Bean("authcConfig")
    public Config config(CasClient casClient, ShiroSessionStore shiroSessionStore) {
        Config config = new Config(casClient);
        config.setSessionStore(shiroSessionStore);
        return config;
    }

    /**
     * 自定义存储
     *
     * @return
     */
    @Bean
    public ShiroSessionStore shiroSessionStore() {
        return new ShiroSessionStore();
    }

    /**
     * cas 客户端配置
     *
     * @param casConfig
     * @return
     */
    @Bean
    public CasClient casClient(CasConfiguration casConfig) {
        CasClient casClient = new CasClient(casConfig);
        //客户端回调地址, 登陆成功后会拦截这个地址
        casClient.setCallbackUrl(projectUrl + "/callback?client_name=" + clientName);
        casClient.setName(clientName);
        return casClient;
    }

    /**
     * 请求cas服务端配置
     *
     * @param
     */
    @Bean
    public CasConfiguration casConfig(){
        final CasConfiguration configuration = new CasConfiguration();
        //CAS server登录地址
        configuration.setLoginUrl(casServerUrl + "/login");
        //CAS 版本，默认为 CAS30，我们使用的是 CAS20
        configuration.setProtocol(CasProtocol.CAS30);
        configuration.setAcceptAnyProxy(true);
        configuration.setPrefixUrl(casServerUrl + "/");
        //监控CAS服务端登出，登出后销毁本地session实现双向登出
        DefaultCasLogoutHandler logoutHandler = new DefaultCasLogoutHandler();
        logoutHandler.setDestroySession(true);
        configuration.setLogoutHandler(logoutHandler);
        return configuration;
    }
}
