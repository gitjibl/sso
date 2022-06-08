package com.example.shirocaspac4j.config;

import io.buji.pac4j.realm.Pac4jRealm;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.subject.PrincipalCollection;

import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName: shiro-cas-pac4j
 * @Package: com.example.shirocaspac4j.config
 * @ClassName: ShiroPac4jRealm
 * @Author: jibl
 * @Description:
 * @Date: 2021/5/7 14:20
 * @Version: 1.0
 */
@Slf4j
public class ShiroPac4jRealm extends Pac4jRealm {

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(final PrincipalCollection principals) {
        Object user = super.getAvailablePrincipal(principals);
        log.info("登录用户：{}", user);
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        List<String> permissions = new ArrayList<String>();
        permissions.add("user:info");
        simpleAuthorizationInfo.addStringPermissions(permissions);

        return simpleAuthorizationInfo;
    }

}
