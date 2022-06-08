package com.example.demo.config;

import org.jasig.cas.client.authentication.AuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @ProjectName: springboot-cas
 * @Package: com.example.demo.config
 * @ClassName: CasConfig
 * @Author: jibl
 * @Description:
 * @Date: 2022/6/8 14:40
 * @Version: 1.0
 */

@Configuration
public class CasConfig {

    @Value("${cas.server-login-url}")
    private String casServerLoginUrl;

    @Value("${cas.client-host-url}")
    private String casClientHostUrl;

    @Value("${cas-ignore-pattern}")
    private String casIgnorePattern;

    /**
     * description:授权过滤器
     * ignoreUrlPatternType 使用CAS现成的正则表达式过滤策略
     */
    @Bean
    public FilterRegistrationBean filterAuthenticationRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new AuthenticationFilter());
        registration.addUrlPatterns("/*");

        Map<String,String> initParameters = new HashMap<String, String>();
        initParameters.put("casServerLoginUrl", casServerLoginUrl);
        initParameters.put("serverName", casClientHostUrl);
        //配置文件中设置要过滤拦截的路径
        initParameters.put("ignorePattern", casIgnorePattern);
        initParameters.put("ignoreUrlPatternType", "org.jasig.cas.client.authentication.RegexUrlPatternMatcherStrategy");
        registration.setInitParameters(initParameters);

        registration.setOrder(1);
        return registration;
    }
}