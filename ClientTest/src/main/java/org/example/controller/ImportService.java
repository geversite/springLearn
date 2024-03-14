package org.example.controller;

import com.zxb.pojo.User;
import org.mySpring.cloud.annotation.FeignClient;

import java.util.List;

@FeignClient("com.zxb.service.UserService")
public interface ImportService {

    public User getUser(int id);

    public int deleteUser(int id);

    public int updateUser(int id, User user);

    public int addUser(User user);

    public List<User> getUsers();
}
