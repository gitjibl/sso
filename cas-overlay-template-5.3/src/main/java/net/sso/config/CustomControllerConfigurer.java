package net.sso.config;

import net.sso.controller.CaptchaController;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration("captchaConfiguration")
@EnableConfigurationProperties(CasConfigurationProperties.class)
public class CustomControllerConfigurer {

    /**
     * 验证码配置,注入bean到spring中
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(name = "captchaController")
    public CaptchaController captchaController() {
        return new CaptchaController();
    }


    /**
     * 自定义SercicesManage管理配置,注入bean到spring中
     *
     * @return
     */
   /* @Bean
    @ConditionalOnMissingBean(name = "servicesManagerController")
    public ServicesManagerController servicesManagerController() {
        return new ServicesManagerController();
    }*/

}
