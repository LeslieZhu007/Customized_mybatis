package com.itheima.sh.dao;

import com.itheima.sh.pojo.User;

import java.util.List;

public interface UserMapper {
    //定义方法
    List<User> queryAllUsers();
}
