package com.example.shirocaspac4j.config;

import io.buji.pac4j.filter.CallbackFilter;
import io.buji.pac4j.filter.SecurityFilter;
import io.buji.pac4j.subject.Pac4jSubjectFactory;
import org.apache.shiro.mgt.SubjectFactory;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.spring.web.config.AbstractShiroWebFilterConfiguration;
import org.apache.shiro.spring.web.config.DefaultShiroFilterChainDefinition;
import org.apache.shiro.spring.web.config.ShiroFilterChainDefinition;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.jasig.cas.client.session.SingleSignOutFilter;
import org.jasig.cas.client.session.SingleSignOutHttpSessionListener;
import org.pac4j.cas.client.CasClient;
import org.pac4j.cas.client.rest.CasRestFormClient;
import org.pac4j.cas.config.CasConfiguration;
import org.pac4j.cas.config.CasProtocol;
import org.pac4j.core.client.Clients;
import org.pac4j.core.config.Config;
import org.pac4j.core.matching.PathMatcher;
import org.pac4j.http.client.direct.ParameterClient;
import org.pac4j.jwt.config.encryption.SecretEncryptionConfiguration;
import org.pac4j.jwt.config.signature.SecretSignatureConfiguration;
import org.pac4j.jwt.credentials.authenticator.JwtAuthenticator;
import org.pac4j.jwt.profile.JwtGenerator;
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

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.Map;

/**
 * @ProjectName: shiro-cas-pac4j
 * @Package: com.example.shirocaspac4j.config
 * @ClassName: ShiroConfiguration
 * @Author: jibl
 * @Description:
 * @Date: 2021/5/7 14:20
 * @Version: 1.0
 */
@Configuration
public class ShiroConfiguration extends AbstractShiroWebFilterConfiguration {

    @Value("#{ @environment['cas.prefixUrl'] ?: null }")
    private String prefixUrl;

    @Value("#{ @environment['cas.loginUrl'] ?: null }")
    private String casLoginUrl;

    @Value("#{ @environment['cas.callbackUrl'] ?: null }")
    private String callbackUrl;

    //jwt??????
    @Value("${jwt.salt}")
    private String salt;


    /**
     * JWT Token ???????????????CommonProfile????????????????????????token??????
     *
     * @return
     */
    @SuppressWarnings("rawtypes")
    @Bean
    protected JwtGenerator jwtGenerator() {
        return new JwtGenerator(new SecretSignatureConfiguration(salt), new SecretEncryptionConfiguration(salt));
    }

    @Bean
    protected JwtAuthenticator jwtAuthenticator() {
        JwtAuthenticator jwtAuthenticator = new JwtAuthenticator();
        jwtAuthenticator.addSignatureConfiguration(new SecretSignatureConfiguration(salt));
        jwtAuthenticator.addEncryptionConfiguration(new SecretEncryptionConfiguration(salt));
        return jwtAuthenticator;
    }

    /**
     * cas???????????????????????????url?????????rest???????????????
     *
     * @return
     */
    @Bean
    public CasConfiguration casConfiguration() {
        CasConfiguration casConfiguration = new CasConfiguration(casLoginUrl);
        casConfiguration.setProtocol(CasProtocol.CAS20);
        casConfiguration.setPrefixUrl(prefixUrl);
        return casConfiguration;
    }

    /**
     * ??????????????????
     *
     * @return
     */
    @Bean
    public PathMatcher pathMatcher() {
        PathMatcher pathMatcher = new PathMatcher();
        pathMatcher.excludePath("/html/**");
        return pathMatcher;
    }

    /**
     * pac4jRealm
     *
     * @return
     */
    @Bean(name = "pac4jRealm")
    public Realm pac4jRealm() {
        return new ShiroPac4jRealm();
    }

    /**
     * ??????rest??????????????????tgt?????????service ticket?????????????????????CasProfile
     *
     * @return
     */
    @Bean
    protected CasRestFormClient casRestFormClient(CasConfiguration casConfiguration) {
        CasRestFormClient casRestFormClient = new CasRestFormClient();
        casRestFormClient.setConfiguration(casConfiguration);
        casRestFormClient.setName("rest");
        return casRestFormClient;
    }

    /**
     * casClient
     *
     * @return
     */
    @Bean
    public CasClient casClient(CasConfiguration casConfiguration) {
        CasClient casClient = new CasClient();
        casClient.setConfiguration(casConfiguration);
        casClient.setCallbackUrl(callbackUrl);
        casClient.setName("cas");
        return casClient;
    }

    /**
     * token????????????
     *
     * @return
     */
    @Bean
    protected Clients clients(CasClient casClient, CasRestFormClient casRestFormClient) {
        //??????????????????client
        Clients clients = new Clients();
        //token?????????????????????HeaderClient?????????
        ParameterClient parameterClient = new ParameterClient("token", jwtAuthenticator());
        parameterClient.setSupportGetRequest(true);
        parameterClient.setName("jwt");
        //?????????client??????????????????
        clients.setClients(casClient, casRestFormClient, parameterClient);
        return clients;
    }

    @Bean
    protected Config casConfig(Clients clients) {
        Config config = new Config();
        config.setClients(clients);
        return config;
    }

    /**
     * ??????cas????????????????????????????????????cas??????????????????
     *
     * @return
     */
    @Bean(name = "subjectFactory")
    protected SubjectFactory subjectFactory() {
        return new Pac4jSubjectFactory();
    }

    /**
     * ???????????????listener
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
     * ????????????filter
     *
     * @return
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public FilterRegistrationBean singleSignOutFilter() {
        FilterRegistrationBean bean = new FilterRegistrationBean();
        bean.setName("singleSignOutFilter");
        SingleSignOutFilter singleSignOutFilter = new SingleSignOutFilter();
        singleSignOutFilter.setCasServerUrlPrefix(prefixUrl);
        singleSignOutFilter.setIgnoreInitConfiguration(true);
        bean.setFilter(singleSignOutFilter);
        bean.addUrlPatterns("/*");
        bean.setEnabled(true);
        return bean;
    }

    @Bean(name = "securityManager")
    public DefaultWebSecurityManager securityManager(Realm pac4jRealm, SubjectFactory subjectFactory) {
        DefaultWebSecurityManager defaultWebSecurityManager = new DefaultWebSecurityManager();
        defaultWebSecurityManager.setRealm(pac4jRealm);
        defaultWebSecurityManager.setSubjectFactory(subjectFactory);
        return defaultWebSecurityManager;
    }

    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new DelegatingFilterProxy("shiroFilter"));
        filterRegistrationBean.addInitParameter("targetFilterLifecycle", "true");
        filterRegistrationBean.setEnabled(true);
        filterRegistrationBean.addUrlPatterns("/*");
        return filterRegistrationBean;
    }

    @Bean(name = "shiroFilter")
    protected ShiroFilterFactoryBean shiroFilterFactoryBean(DefaultWebSecurityManager securityManager, Config config) {
        ShiroFilterFactoryBean filterFactoryBean = super.shiroFilterFactoryBean();
        filterFactoryBean.setSecurityManager(securityManager);

        //???????????????
        Map<String, Filter> filters = new HashMap<String, Filter>();
        SecurityFilter securityFilter = new SecurityFilter();
        securityFilter.setClients("cas,rest,jwt");
        securityFilter.setConfig(config);
        filters.put("casSecurityFilter", securityFilter);

        CallbackFilter callbackFilter = new CallbackFilter();
        callbackFilter.setConfig(config);
        filters.put("callbackFilter", callbackFilter);

        filterFactoryBean.setFilters(filters);


        return filterFactoryBean;
    }

    @Bean
    public ShiroFilterChainDefinition shiroFilterChainDefinition() {
        DefaultShiroFilterChainDefinition definition = new DefaultShiroFilterChainDefinition();
        definition.addPathDefinition("/callback", "callbackFilter");
        definition.addPathDefinition("/you", "anon");
        definition.addPathDefinition("/user/info", "anon");
        definition.addPathDefinition("/**", "casSecurityFilter");

        return definition;
    }

    /**
     * ??????Shiro?????????(???@RequiresRoles,@RequiresPermissions),?????????SpringAOP????????????Shiro????????????,???????????????????????????????????????
     * ??????????????????bean(DefaultAdvisorAutoProxyCreator(??????)???AuthorizationAttributeSourceAdvisor)?????????????????????
     *
     * @return
     */
    @Bean
    @DependsOn({"lifecycleBeanPostProcessor"})
    public DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        advisorAutoProxyCreator.setProxyTargetClass(true);
        return advisorAutoProxyCreator;
    }

    /**
     * ?????? shiro aop????????????
     *
     * @param securityManager
     * @return
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(DefaultWebSecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor aasa = new AuthorizationAttributeSourceAdvisor();
        aasa.setSecurityManager(securityManager);
        return aasa;
    }
}
