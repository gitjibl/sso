package net.sso.authentication;

import cn.hutool.core.map.MapUtil;
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
import org.springframework.util.StringUtils;
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


//        org.apereo.cas.authentication.UsernamePasswordCredential
        UsernamePasswordCredential usernamePasswordCredential = null;
        CustomCredential customCredential = null;

        String username = "";
        String password = "";
        String kaptcha = "";

        String className = credential.getClass().getName();
        if (className.indexOf("UsernamePasswordCredential") != -1) {
            System.out.println("UsernamePasswordCredential认证...");
            usernamePasswordCredential = (UsernamePasswordCredential) credential;
            username = usernamePasswordCredential.getUsername();
            password = usernamePasswordCredential.getPassword();
            kaptcha = "";
        } else {
            System.out.println("CustomCredential认证...");
            customCredential = (CustomCredential) credential;
            username = customCredential.getUsername();
            password = customCredential.getPassword();
            kaptcha = customCredential.getKaptcha();
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            String kaptcha_code = attributes.getRequest().getSession().getAttribute("kaptcha_code").toString();
            if (!kaptcha.equalsIgnoreCase(kaptcha_code)) {
                throw new CheckCodeErrorException();
            }
        }



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

        System.out.println("database username : " + info.getUsername());
        System.out.println("database password : " + info.getPassword());


        if (info.getExpired() == 1) {
            //用户锁定
            throw new AccountLockedException();
        }
        if (info.getDisabled() == 1) {
            //用户禁用
            throw new AccountDisabledException();
        }

        /**
         * 用户名密码异常
         */
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
            if (className.indexOf("UsernamePasswordCredential") != -1) {
                return createHandlerResult(usernamePasswordCredential,
                        this.principalFactory.createPrincipal(username, returnInfo), list);
            } else {
                return createHandlerResult(customCredential,
                        this.principalFactory.createPrincipal(username, returnInfo), list);
            }

        }
    }

//    private AuthenticationHandlerExecutionResult doTokenAuthentication(Credential credential) throws FailedLoginException {
//        TokenCredential tokenCredential = (TokenCredential) credential;
//        Map<String, Object> dbFields = this.getJdbcTemplate().queryForMap(this.sql, tokenCredential.getId());
//        if (MapUtil.isNotEmpty(dbFields)) {
//            return createHandlerResult(
//                    tokenCredential,
//                    principalFactory.createPrincipal(tokenCredential.getId(), new HashMap<>()),
//                    new ArrayList<>()
//            );
//        }
//        throw new FailedLoginException("用户验证错误");
//    }
}
