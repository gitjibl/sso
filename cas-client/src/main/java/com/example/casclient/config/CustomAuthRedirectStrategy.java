package com.example.casclient.config;

import org.jasig.cas.client.authentication.AuthenticationRedirectStrategy;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomAuthRedirectStrategy implements AuthenticationRedirectStrategy {

    @Override
    public void redirect(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, String s) throws IOException {
        httpServletResponse.setCharacterEncoding("utf-8");
        httpServletResponse.setContentType("application/json; charset=utf-8");
        httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
        httpServletResponse.setHeader("Access-Control-Expose-Headers", "TOKEN_URL");
        httpServletResponse.setHeader("TOKEN_URL", "https://192.168.0.113:8443/cas/login?service=http://192.168.0.113:8012/redirect?url=");
    }

}

