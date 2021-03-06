package com.example.vuecasshiroclient.config;

import com.example.vuecasshiroclient.filter.JwtFilter;
import io.buji.pac4j.filter.CallbackFilter;
import io.buji.pac4j.filter.LogoutFilter;
import io.buji.pac4j.filter.SecurityFilter;
import io.buji.pac4j.subject.Pac4jSubjectFactory;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.jasig.cas.client.session.SingleSignOutFilter;
import org.jasig.cas.client.session.SingleSignOutHttpSessionListener;
import org.pac4j.core.config.Config;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.DelegatingFilterProxy;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @ProjectName: vue-cas-shiro-client
 * @Package: com.example.vuecasshiroclient.config
 * @ClassName: ShiroConfig
 * @Author: jibl
 * @Description:
 * @Date: 2021/4/19 11:43
 * @Version: 1.0
 */
@Configuration
public class ShiroConfig {
    /**
     * 项目工程路径
     */
    @Value("${cas.project.url}")
    private String projectUrl;

    /**
     * 项目cas服务路径
     */
    @Value("${cas.server.url}")
    private String casServerUrl;

    /**
     * 客户端名称
     */
    @Value("${cas.client-name}")
    private String clientName;


    /**
     * 单点登出的listener
     *
     * @return
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Bean
    public ServletListenerRegistrationBean<?> singleSignOutHttpSessionListener() {
        ServletListenerRegistrationBean bean = new ServletListenerRegistrationBean();
        bean.setListener(new SingleSignOutHttpSessionListener());
        bean.setEnabled(true);
        return bean;
    }

    /**
     * 单点登出filter
     *
     * @return
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public FilterRegistrationBean singleSignOutFilter() {
        FilterRegistrationBean bean = new FilterRegistrationBean();
        bean.setName("singleSignOutFilter");
        SingleSignOutFilter singleSignOutFilter = new SingleSignOutFilter();
        singleSignOutFilter.setCasServerUrlPrefix(casServerUrl);
        singleSignOutFilter.setIgnoreInitConfiguration(true);
        bean.setFilter(singleSignOutFilter);
        bean.addUrlPatterns("/*");
        bean.setEnabled(true);
        return bean;
    }


    @Bean("securityManager")
    public DefaultWebSecurityManager securityManager(Pac4jSubjectFactory subjectFactory, CasRealm casRealm) {
        DefaultWebSecurityManager manager = new DefaultWebSecurityManager();
        manager.setRealm(casRealm);
        manager.setSubjectFactory(subjectFactory);
//        manager.setSessionManager(sessionManager);	//去掉session管理
        return manager;
    }

    @Bean
    public CasRealm casRealm() {
        CasRealm realm = new CasRealm();
        // 使用自定义的realm
        realm.setClientName(clientName);
        realm.setCachingEnabled(false);
        //暂时不使用缓存
        realm.setAuthenticationCachingEnabled(false);
        realm.setAuthorizationCachingEnabled(false);
        //realm.setAuthenticationCacheName("authenticationCache");
        //realm.setAuthorizationCacheName("authorizationCache");
        return realm;
    }

    /**
     * 使用 pac4j 的 subjectFactory
     *
     * @return
     */
    @Bean
    public Pac4jSubjectFactory subjectFactory() {
        return new Pac4jSubjectFactory();
    }

    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean filterRegistration = new FilterRegistrationBean();
        filterRegistration.setFilter(new DelegatingFilterProxy("shiroFilter"));
        //  该值缺省为false,表示生命周期由SpringApplicationContext管理,设置为true则表示由ServletContainer管理
        filterRegistration.addInitParameter("targetFilterLifecycle", "true");
        filterRegistration.setEnabled(true);
        filterRegistration.addUrlPatterns("/*");
        filterRegistration.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.FORWARD);
        return filterRegistration;
    }

    /**
     * 加载shiroFilter权限控制规则（从数据库读取然后配置）
     *
     * @param shiroFilterFactoryBean
     */
    private void loadShiroFilterChain(ShiroFilterFactoryBean shiroFilterFactoryBean) {
        /*下面这些规则配置最好配置到配置文件中 */
        Map<String, String> filterMap = new LinkedHashMap<>();
        //cas 资源认证拦截器
        filterMap.put("/ssoLogin", "securityFilter");
        filterMap.put("/", "securityFilter");
        filterMap.put("/callback/**", "callbackFilter");
        filterMap.put("/ssoLogout", "ssoLogoutFilter");
        filterMap.put("/user/test", "anon");
        //使用自己的过滤器
        filterMap.put("/redirect", "anon");
        filterMap.put("/**", "jwt");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterMap);
    }


    /**
     * shiroFilter
     *
     * @param securityManager
     * @param config
     * @return
     */
    @Bean("shiroFilter")
    public ShiroFilterFactoryBean factory(DefaultWebSecurityManager securityManager, Config config) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        // 必须设置 SecurityManager
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        Map<String, Filter> filters = new HashMap<>();
        //cas 资源认证拦截器
        SecurityFilter securityFilter = new SecurityFilter();
        securityFilter.setConfig(config);
        securityFilter.setClients(clientName);
        filters.put("securityFilter", securityFilter);
        //cas 认证后回调拦截器
        CallbackFilter callbackFilter = new CallbackFilter();
        callbackFilter.setConfig(config);
        callbackFilter.setDefaultUrl(projectUrl);
        filters.put("callbackFilter", callbackFilter);
        // 注销 拦截器
        LogoutFilter logoutFilter = new LogoutFilter();
        logoutFilter.setConfig(config);
        logoutFilter.setCentralLogout(true);
        logoutFilter.setLocalLogout(true);
        logoutFilter.setDefaultUrl(projectUrl + "/callback?client_name=" + clientName);
        filters.put("ssoLogoutFilter", logoutFilter);
        //验证请求拦截器
        //添加自己的过滤器
        filters.put("jwt", new JwtFilter());
        shiroFilterFactoryBean.setFilters(filters);
        // 添加casFilter到shiroFilter中
        loadShiroFilterChain(shiroFilterFactoryBean);
        return shiroFilterFactoryBean;
    }


    /**
     * 下面的代码是添加注解支持
     */
    @Bean
    @DependsOn("lifecycleBeanPostProcessor")
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        // 强制使用cglib，防止重复代理和可能引起代理出错的问题
        // https://zhuanlan.zhihu.com/p/29161098
        defaultAdvisorAutoProxyCreator.setProxyTargetClass(true);
        return defaultAdvisorAutoProxyCreator;
    }

    @Bean
    public static LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(DefaultWebSecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager);
        return advisor;
    }

}
