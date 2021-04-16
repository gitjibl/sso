package com.example.casshiroclient.config;

import io.buji.pac4j.realm.Pac4jRealm;
import io.buji.pac4j.subject.Pac4jPrincipal;
import io.buji.pac4j.token.Pac4jToken;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.jasig.cas.client.util.AbstractCasFilter;
import org.jasig.cas.client.validation.Assertion;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.InternalAttributeHandler;
import org.pac4j.core.profile.ProfileHelper;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.*;

/**
 * @ProjectName: cas-shiro-client
 * @Package: com.example.casshiroclient.config
 * @ClassName: CasRealm
 * @Author: jibl
 * @Description:
 * @Date: 2021/4/16 9:08
 * @Version: 1.0
 */
public class CasRealm extends Pac4jRealm {
    private String clientName;

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    /**
     * 认证
     *
     * @param authenticationToken
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        final Pac4jToken pac4jToken = (Pac4jToken) authenticationToken;
        final List<CommonProfile> commonProfileList = pac4jToken.getProfiles();
        final CommonProfile commonProfile = commonProfileList.get(0);
        System.out.println("单点登录返回的信息" + commonProfile.toString());
        //todo
        final Pac4jPrincipal principal = new Pac4jPrincipal(commonProfileList, getPrincipalNameAttribute());
        final PrincipalCollection principalCollection = new SimplePrincipalCollection(principal, getName());
        return new SimpleAuthenticationInfo(principalCollection, commonProfileList.hashCode());
    }

    /**
     * 授权/验权（todo 后续有权限在此增加）
     *
     * @param principals
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Pac4jPrincipal principal = (Pac4jPrincipal)request.getUserPrincipal();
        List<CommonProfile> profiles = principal.getProfiles();
        Map<String, Object> attributes = profiles.get(0).getAttributes();
        String username = attributes.get("username").toString();
        SimpleAuthorizationInfo authInfo = new SimpleAuthorizationInfo();
        if(username.equals("admin")){
            Collection<String> roles = new HashSet<>();
            roles.add("admin");
            authInfo.addRoles(roles);
            Collection<String> permissions = new HashSet<>();
            permissions.add("admin:add");
            permissions.add("admin:update");
            authInfo.addStringPermissions(permissions);
        }else{
            Collection<String> roles = new HashSet<>();
            roles.add("test");
            authInfo.addRoles(roles);
            Collection<String> permissions = new HashSet<>();
            permissions.add("test:add");
            permissions.add("test:update");
            authInfo.addStringPermissions(permissions);
        }
        return authInfo;
    }

}
