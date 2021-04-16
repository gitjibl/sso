package net.sso.authentication;

import net.sso.entity.CustomCredential;
import net.sso.entity.User;
import net.sso.exception.CheckCodeErrorException;
import net.sso.exception.UserNameAndPasswordException;
import org.apereo.cas.authentication.*;
import org.apereo.cas.authentication.exceptions.AccountDisabledException;
import org.apereo.cas.authentication.handler.support.AbstractPreAndPostProcessingAuthenticationHandler;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.services.ServicesManager;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.security.auth.login.AccountException;
import javax.security.auth.login.AccountLockedException;
import javax.security.auth.login.FailedLoginException;
import java.security.GeneralSecurityException;
import java.util.*;

public class CustomerHandlerAuthentication extends AbstractPreAndPostProcessingAuthenticationHandler {

    public CustomerHandlerAuthentication(String name, ServicesManager servicesManager, PrincipalFactory principalFactory, Integer order) {
        super(name, servicesManager, principalFactory, order);
    }

    @Override
    public boolean supports(Credential credential) {
        //判断传递过来的Credential 是否是自己能处理的类型
        return credential instanceof UsernamePasswordCredential;
    }

    @Override
    protected AuthenticationHandlerExecutionResult doAuthentication(Credential credential) throws GeneralSecurityException, PreventedException {

        CustomCredential customCredential = (CustomCredential) credential;

        String username = customCredential.getUsername();
        String password = customCredential.getPassword();
        String kaptcha = customCredential.getKaptcha();

        System.out.println("sso自定义认证策略.................");
        System.out.println("username : " + username);
        System.out.println("password : " + password);
        System.out.println("kaptcha : " + kaptcha);



        // JDBC模板依赖于连接池来获得数据的连接，所以必须先要构造连接池
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/cas?useSSL=false&serverTimezone=UTC");
        dataSource.setUsername("root");
        dataSource.setPassword("123456");

        // 创建JDBC模板
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        jdbcTemplate.setDataSource(dataSource);

        String sql = "SELECT * FROM user WHERE username = ?";

        User info = (User) jdbcTemplate.queryForObject(sql, new Object[]{username}, new BeanPropertyRowMapper(User.class));

        System.out.println("database username : "+ info.getUsername());
        System.out.println("database password : "+ info.getPassword());

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String right = attributes.getRequest().getSession().getAttribute("kaptcha_code").toString();

        if (info.getExpired() == 1) {
            //用户锁定
            throw new AccountLockedException();
        }
        if (info.getDisabled() == 1) {
            //用户禁用
            throw new AccountDisabledException();
        }
        if(!kaptcha.equalsIgnoreCase(right)){
            throw new CheckCodeErrorException();
        }

        if (info == null) {
            throw new UserNameAndPasswordException();
        }

        if (!info.getPassword().equals(password)) {
            throw new UserNameAndPasswordException();
        } else {
            //可自定义返回给客户端的多个属性信息
            HashMap<String, Object> returnInfo = new HashMap<>();
            returnInfo.put("username", info.getUsername());
            returnInfo.put("expired", info.getExpired());
            returnInfo.put("disable", info.getDisabled());
            final List<MessageDescriptor> list = new ArrayList<>();

            return createHandlerResult(customCredential,
                    this.principalFactory.createPrincipal(username, returnInfo), list);
        }
    }
}
