package com.haizhi.authcenter.security;

import com.haizhi.authcenter.bean.User;
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

import java.util.HashSet;
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

        Set<String> permissions = new HashSet<>();
        for(String roleType : roles){
            permissions.addAll(this.userService.findPermissions(roleType));
        }
        authorizationInfo.setStringPermissions(permissions);
        return authorizationInfo;
    }

    //认证
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token)
            throws AuthenticationException {
        String username = token.getPrincipal().toString();

        User user = userService.findByUsername(username);
        if(user == null) {
            throw new UnknownAccountException();//没找到帐号
        }
        if(UserStatus.LOCKED.equals(user.getUserStatus())) {
            throw new LockedAccountException(); //帐号锁定
        }

        String password = token.getCredentials().toString();

        //checkPassword(password,user);

        //交给AuthenticatingRealm使用CredentialsMatcher进行密码匹配，如果觉得人家的不好可以在此判断或自定义实现
        AuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
                user.getUsername(), //用户名
                user.getPassword(), //密码
                ByteSource.Util.bytes(user.getSalt()),//salt=username+salt
                getName()  //realm name
        );

        return authenticationInfo;
    }

    /*
    private void checkPassword(String password,User user) throws CredentialsException{
        throw new CredentialsException();
    }*/

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
