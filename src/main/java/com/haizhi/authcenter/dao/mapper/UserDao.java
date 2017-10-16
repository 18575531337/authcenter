package com.haizhi.authcenter.dao.mapper;

import com.haizhi.authcenter.bean.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by JuniFire on 2017/10/11.
 */
public interface UserDao {

    Map getUser(@Param("username")String username);

    String getRoleStr(@Param("username")String username);

    List<User> test();

}
