package com.example.vuecasclient.config;

import com.example.vuecasclient.filter.MyAuthenticationFilter;
import org.jasig.cas.client.session.SingleSignOutFilter;
import org.jasig.cas.client.session.SingleSignOutHttpSessionListener;
import org.jasig.cas.client.util.AssertionThreadLocalFilter;
import org.jasig.cas.client.util.HttpServletRequestWrapperFilter;
import org.jasig.cas.client.validation.Cas30ProxyReceivingTicketValidationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class CasConfig {

    @Autowired
    private CasProperties casProperties;

    public CasConfig() {
    }

//    @Bean
//    public CookieSerializer httpSessionIdResolver() {
//        DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();
//        cookieSerializer.setUseHttpOnlyCookie(false);
//        cookieSerializer.setSameSite("None");
//        cookieSerializer.setCookiePath("/");
//        cookieSerializer.setUseSecureCookie(true);
//        return cookieSerializer;
//    }

    @Bean
    public CasProperties getCasProperties()
    {
        return new CasProperties();
    }

    @Bean
    public ServletListenerRegistrationBean<SingleSignOutHttpSessionListener> singleSignOutHttpSessionListener() {
        ServletListenerRegistrationBean<SingleSignOutHttpSessionListener> listener = new ServletListenerRegistrationBean<SingleSignOutHttpSessionListener>();
        listener.setEnabled(casProperties.getEnable());
        listener.setListener(new SingleSignOutHttpSessionListener());
        listener.setOrder(1);
        return listener;
    }

    /**
     * 该过滤器用于实现单点登出功能，单点退出配置，一定要放在其他filter之前
     *
     * @return
     */
    @Bean
    public FilterRegistrationBean singleSignOutFilter() {
        FilterRegistrationBean filterRegistration = new FilterRegistrationBean();
        filterRegistration.setFilter(new SingleSignOutFilter());
        filterRegistration.setEnabled(casProperties.getEnable());
        filterRegistration.addUrlPatterns("/*");
        filterRegistration.addInitParameter("casServerUrlPrefix", casProperties.getServerUrlPrefix());
        filterRegistration.setOrder(3);
        return filterRegistration;
    }

    /**
     * 该过滤器负责用户的认证工作
     *
     * @return
     */
    @Bean
    public FilterRegistrationBean authenticationFilter() {
        FilterRegistrationBean authenticationFilter = new FilterRegistrationBean();
        authenticationFilter.setFilter(new MyAuthenticationFilter());
        authenticationFilter.addUrlPatterns("/*");
        Map<String, String> initParameters = new HashMap<String, String>();
        initParameters.put("ignorePattern", casProperties.getIgnorePattern());
        initParameters.put("casServerLoginUrl", casProperties.getServerLoginUrl());
        initParameters.put("serverName", casProperties.getClientHostUrl());
        authenticationFilter.setInitParameters(initParameters);
        authenticationFilter.setOrder(2);
        List<String> urlPatterns = new ArrayList<String>();
        // 设置匹配的url
        authenticationFilter.setUrlPatterns(urlPatterns);
        return authenticationFilter;
    }

    /**
     * 该过滤器负责对Ticket的校验工作,使用CAS 3.0协议
     *
     * @return
     */
    @Bean
    public FilterRegistrationBean cas30ProxyReceivingTicketValidationFilter() {
        FilterRegistrationBean filterRegistration = new FilterRegistrationBean();
        filterRegistration.setFilter(new Cas30ProxyReceivingTicketValidationFilter());
        filterRegistration.setEnabled(casProperties.getEnable());
        filterRegistration.addUrlPatterns("/*");
        filterRegistration.addInitParameter("casServerUrlPrefix", casProperties.getServerUrlPrefix());
        filterRegistration.addInitParameter("serverName", casProperties.getClientHostUrl());
        filterRegistration.setOrder(5);
        return filterRegistration;
    }

    /**
     * request wraper过滤器
     * @return
     */
    @Bean
    public FilterRegistrationBean httpServletRequestWrapperFilter() {
        FilterRegistrationBean filterRegistration = new FilterRegistrationBean();
        filterRegistration.setFilter(new HttpServletRequestWrapperFilter());
        filterRegistration.setEnabled(true);
        filterRegistration.addUrlPatterns("/*");
        filterRegistration.setOrder(6);
        return filterRegistration;
    }

    /**
     * 该过滤器使得可以通过org.jasig.cas.client.util.AssertionHolder来获取用户的登录名。
     * 比如AssertionHolder.getAssertion().getPrincipal().getName()。
     * 这个类把Assertion信息放在ThreadLocal变量中，这样应用程序不在web层也能够获取到当前登录信息
     *
     * @return
     */
    @Bean
    public FilterRegistrationBean assertionThreadLocalFilter() {
        FilterRegistrationBean filterRegistration = new FilterRegistrationBean();
        filterRegistration.setFilter(new AssertionThreadLocalFilter());
        filterRegistration.setEnabled(true);
        filterRegistration.addUrlPatterns("/*");
        filterRegistration.setOrder(7);
        return filterRegistration;
    }
}