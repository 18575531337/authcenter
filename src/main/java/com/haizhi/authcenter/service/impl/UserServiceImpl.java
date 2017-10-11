package com.haizhi.authcenter.service.impl;

import com.haizhi.authcenter.bean.User;
import com.haizhi.authcenter.dao.mapper.UserDao;
import com.haizhi.authcenter.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Created by JuniFire on 2017/10/11.
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void login(User user) {

    }

    @Override
    public void login(User user, String password) {

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
    public User findByUsername(String username) {
        return null;
    }

    @Override
    public Set<String> findRoles(String username) {
        return null;
    }

    @Override
    public Set<String> findPermissions(String username) {
        return null;
    }

    @Override
    public User test() {
        return this.userDao.test().get(0);
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }
}
