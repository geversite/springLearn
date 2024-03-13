package com.zxb.service;

import com.zxb.mapper.UserMapper;
import com.zxb.pojo.User;
import org.mySpring.annotation.Autowired;
import org.mySpring.annotation.Service;
import org.myBatis.session.SqlSession;
import org.myBatis.session.SqlSessionFactory;
import org.myBatis.session.SqlSessionFactoryBuilder;
import org.mySpring.cloud.RPCService;

import java.util.List;


@RPCService()
public class UserService {

    @Autowired
    OrderService orderService;

    @Autowired
    UserMapper userMapper;

    public UserService() throws Exception {

    }


    public User getUser(int id){
        return userMapper.getUserById(id);
    }

    public int deleteUser(int id) {
        return userMapper.deleteUserById(id);
    }

    public int updateUser(int id, User user) {
        return userMapper.updateUser(id,user);
    }

    public int addUser(User user) {
        return userMapper.insertUser(user);
    }

    public List<User> getUsers() {
        return userMapper.getUsers();
    }
}
