package com.haizhi.authcenter.service;

import com.haizhi.authcenter.entity.User;

import java.util.Set;

/**
 * Created by haizhi on 2017/10/9.
 */
public interface UserService {

    String getName();

    void login(User user);
    void login(User user,String password);
    void logout();
    void logout(User user);

    User createUser(User user); //创建账户
    void changePassword(User user);//修改密码
    void correlationRoles(User user); //添加用户-角色关系
    void uncorrelationRoles(User user);// 移除用户-角色关系

    User findUser(String username);// 根据用户名查找用户
    Set<String> findRoles(String username);// 根据用户名查找其角色
    Set<String> findPermissions(Set<String> roleType); //根据用户名查找其权限

    User test();
}
