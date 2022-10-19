package com.example.casclient;

import net.unicon.cas.client.configuration.EnableCasClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

@SpringBootApplication
@EnableCasClient
public class CasClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(CasClientApplication.class, args);
    }

//    @Bean
//    public CookieSerializer httpSessionIdResolver() {
//        DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();
//        cookieSerializer.setCookieName("JSESSIONID");
//        cookieSerializer.setDomainName("localhost");
//        cookieSerializer.setCookiePath("/");
//        cookieSerializer.setUseHttpOnlyCookie(false);
//        cookieSerializer.setSameSite("Lax");
//        cookieSerializer.setUseSecureCookie(false);
//        return cookieSerializer;
//    }

}
