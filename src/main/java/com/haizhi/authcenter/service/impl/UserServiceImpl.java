package com.haizhi.authcenter.service.impl;

import com.haizhi.authcenter.cache.impl.CacheToken;
import com.haizhi.authcenter.entity.User;
import com.haizhi.authcenter.dao.mapper.PermissionDao;
import com.haizhi.authcenter.dao.mapper.UserDao;
import com.haizhi.authcenter.service.UserService;
import com.haizhi.authcenter.util.Utils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by JuniFire on 2017/10/11.
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private PermissionDao permissionDao;

    @Autowired
    private CacheToken cacheToken;

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void login(User user) {
        UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(
                user.getUsername(),user.getPassword());
        usernamePasswordToken.setRememberMe(true);//关闭浏览器后不必重新登陆

        Subject subject = SecurityUtils.getSubject();
        subject.login(usernamePasswordToken);
    }

    @Override
    public void login(User user, String password) {

    }

    @Override
    public void logout() {
        String id = Utils.getUserID();
        this.cacheToken.del("user_session_"+id);
        SecurityUtils.getSubject().logout();
    }

    @Override
    public void logout(User user) {

    }

    @Override
    public User createUser(User user) {
        return null;
    }

    @Override
    public void changePassword(User user) {

    }

    @Override
    public void correlationRoles(User user) {

    }

    @Override
    public void uncorrelationRoles(User user) {

    }

    @Override
    public User findUser(String username) {
        Map<String,Object> map = userDao.getUser(username);
        User user = new User();
        user.setUsername(map.get("username").toString());
        user.setPassword(map.get("password").toString());
        user.setSalt(map.get("salt").toString());
        Set<String> roles = new HashSet<>();
        for(String role : map.get("role").toString().split(",")){
            roles.add(role);
        }
        user.setRoles(roles);
        user.setId(Long.valueOf(map.get("id").toString()));
        return user;
    }

    @Override
    public Set<String> findRoles(String username) {
        String roleStr = this.userDao.getRoleStr(username);
        Set<String> roles = new HashSet<>();
        for(String role : roleStr.split(",")){
            roles.add(role);
        }
        return roles;
    }

    @Override
    public Set<String> findPermissions(Set<String> roles) {
        Set<String> permissions = new HashSet<>();
        for(String role : roles){
            for(String permission : this.permissionDao.getPermissionStr(role).split(",")){
                permissions.add(permission);
            }
        }
        return permissions;
    }

    @Override
    public User test() {
        return this.userDao.test().get(0);
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public void setPermissionDao(PermissionDao permissionDao) {
        this.permissionDao = permissionDao;
    }

    public void setCacheToken(CacheToken cacheToken) {
        this.cacheToken = cacheToken;
    }
}
