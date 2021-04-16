package net.sso.exception;

import org.apereo.cas.authentication.AuthenticationException;

/**
 * 验证码异常类
 */
public class CheckCodeErrorException extends AuthenticationException {
    public CheckCodeErrorException(){
        super();
    }

    public CheckCodeErrorException(String msg) {
        super(msg);
    }
}
