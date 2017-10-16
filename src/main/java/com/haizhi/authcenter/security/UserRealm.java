package com.haizhi.authcenter.security;

import com.haizhi.authcenter.entity.User;
import com.haizhi.authcenter.constants.UserStatus;
import com.haizhi.authcenter.service.UserService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Created by haizhi on 2017/10/9.
 */
@Component
public class UserRealm extends AuthorizingRealm {

    @Autowired
    UserService userService;

    //授权
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        String username = principals.getPrimaryPrincipal().toString();
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        Set<String> roles = userService.findRoles(username);
        authorizationInfo.setRoles(roles);

        Set<String> permissions = this.userService.findPermissions(roles);

        authorizationInfo.setStringPermissions(permissions);
        return authorizationInfo;
    }

    //认证
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token)
            throws AuthenticationException {
        String username = token.getPrincipal().toString();


        User user = userService.findUser(username);
        /*for test
        User user = new User();
        user.setUsername("aaa");
        user.setPassword("793c5d791a2f74aaf6214aabcdfc11859d7fbf55313b675e215796fb4418502dc9523f5a5e06c3d94942fc71285a854e7910b3eafd470f5f1e3de8aafb5468b5");
        user.setSalt(Key.SALT);
*/
        if(user == null) {
            throw new UnknownAccountException();//没找到帐号
        }
        if(UserStatus.LOCKED.equals(user.getUserStatus())) {
            throw new LockedAccountException(); //帐号锁定
        }

        //交给AuthenticatingRealm使用CredentialsMatcher进行密码匹配，如果觉得人家的不好可以在此判断或自定义实现
        AuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
                user.getUsername(), //用户名
                user.getPassword(), //密码
                ByteSource.Util.bytes(user.getSalt()),//salt=username+salt
                getName()  //realm name
        );

        return authenticationInfo;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
