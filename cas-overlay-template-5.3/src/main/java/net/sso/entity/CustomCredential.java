package net.sso.entity;

import org.apereo.cas.authentication.UsernamePasswordCredential;

import javax.validation.constraints.Size;

/**
 * 登录表单类(扩展)
 */
public class CustomCredential extends UsernamePasswordCredential {

    // 验证码
    @Size(min = 6, max = 6, message = "required.kapcha")
    private String kaptcha;

    public String getKaptcha() {
        return kaptcha;
    }

    public void setKaptcha(String kaptcha) {
        this.kaptcha = kaptcha;
    }
}
