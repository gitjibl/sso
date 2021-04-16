package net.sso.exception;

import org.apereo.cas.authentication.AuthenticationException;
/**用户名密码异常**/
public class UserNameAndPasswordException extends AuthenticationException {
    public UserNameAndPasswordException(){
        super();
    }

    public UserNameAndPasswordException(String msg) {
        super(msg);
    }
}
