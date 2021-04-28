package com.example.vuecasshiroclient.config;

import com.example.vuecasshiroclient.entity.JwtToken;
import com.example.vuecasshiroclient.entity.User;
import com.example.vuecasshiroclient.util.JwtUtil;
import io.buji.pac4j.realm.Pac4jRealm;
import io.buji.pac4j.subject.Pac4jPrincipal;
import io.buji.pac4j.token.Pac4jToken;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.pac4j.core.profile.CommonProfile;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @ProjectName: vue-cas-shiro-client
 * @Package: com.example.vuecasshiroclient.config
 * @ClassName: CasRealm
 * @Author: jibl
 * @Description:
 * @Date: 2021/4/19 11:48
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
     * @param authenticationToken
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken)  {
        if (!(authenticationToken instanceof JwtToken)) {
            final Pac4jToken pac4jToken = (Pac4jToken) authenticationToken;
            final List<CommonProfile> commonProfileList = pac4jToken.getProfiles();
            final CommonProfile commonProfile = commonProfileList.get(0);
            System.out.println("单点登录返回的信息" + commonProfile.toString());
            //todo
            final Pac4jPrincipal principal = new Pac4jPrincipal(commonProfileList, getPrincipalNameAttribute());
            final PrincipalCollection principalCollection = new SimplePrincipalCollection(principal, getName());
            return new SimpleAuthenticationInfo(principalCollection, commonProfileList.hashCode());

        } else {
            // 这里的 token是从 JWTFilter 的 executeLogin 方法传递过来的
            System.out.println(authenticationToken.getCredentials());
            String token = (String) authenticationToken.getCredentials();
            String username = JwtUtil.getUsername(token);
//            User user = userService.getBeanByAccount(username);
            User user = new User();
            if (user == null) {
                throw new AuthenticationException("用户名或密码错误");
            }
            if (!JwtUtil.verify(token, username, user.getPassword())) {
                throw new AuthenticationException("token校验不通过");
            }
            return new SimpleAuthenticationInfo(token, token, getName());
        }
    }


    /**
     * 授权/验权（todo 后续有权限在此增加）
     * @param principals
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {

//        String username = JwtUtil.getUsername(principals.toString());
        String username = ((Pac4jPrincipal) principals.getPrimaryPrincipal()).getName();
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();

       /* // 获取用户角色集
        Set<String> roleSet = userManager.getUserRoles(username);
        simpleAuthorizationInfo.setRoles(roleSet);

        // 获取用户权限集
        Set<String> permissionSet = userManager.getUserPermissions(username);
        simpleAuthorizationInfo.setStringPermissions(permissionSet);*/
        if(username.equals("admin")){
            Collection<String> roles = new HashSet<>();
            roles.add("admin");
            simpleAuthorizationInfo.addRoles(roles);
            Collection<String> permissions = new HashSet<>();
            permissions.add("admin:add");
            permissions.add("admin:update");
            simpleAuthorizationInfo.addStringPermissions(permissions);
        }else{
            Collection<String> roles = new HashSet<>();
            roles.add("test");
            simpleAuthorizationInfo.addRoles(roles);
            Collection<String> permissions = new HashSet<>();
            permissions.add("test:add");
            permissions.add("test:update");
            simpleAuthorizationInfo.addStringPermissions(permissions);
        }
        return simpleAuthorizationInfo;
    }

    /**
     * @author jay
     * 需要重写 AuthorizingRealm（被Pac4jRealm继承）的supports方法，增加对JWTToken的支持
     * 否则会报错：does not support authentication token
     * @param token
     * @return
     */
    @Override
    public boolean supports(AuthenticationToken token) {

        Boolean flag = false;
        if (super.supports(token) ||  token instanceof Pac4jToken || token instanceof JwtToken ) {
            flag = true;
        }
        return flag;
    }

}
