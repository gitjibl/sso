package com.example.casclient.config;

import net.unicon.cas.client.configuration.CasClientConfigurerAdapter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class CasClientConfig extends CasClientConfigurerAdapter {
    @Override
    public void configureAuthenticationFilter(FilterRegistrationBean authenticationFilter) {
        super.configureAuthenticationFilter(authenticationFilter);
        Map<String, String> initParameters = authenticationFilter.getInitParameters();
        initParameters.put("authenticationRedirectStrategyClass",
                "com.example.casclient.config.CustomAuthRedirectStrategy");
        // 配置地址，这里还可以配置很多，例如cas重定向策略等。
        initParameters.put("ignorePattern", "/test|/ignoreUrl2/|/ignoreUrl3/");
    }

}

