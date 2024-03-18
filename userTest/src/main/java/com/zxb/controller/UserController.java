package com.zxb.controller;

import com.zxb.pojo.User;
import com.zxb.service.UserService;
import org.mySpring.annotation.Autowired;
import org.mySpring.annotation.Controller;
import org.mySpring.annotation.RequestBody;
import org.mySpring.annotation.ResponseBody;
import org.mySpring.web.annotation.RequestMapping;
import org.myHttp.entity.HttpRequest;
import org.myHttp.entity.HttpResponse;

import java.util.List;


@Controller
public class UserController {

    @Autowired
    UserService userService;

    @RequestMapping("/users")
    @ResponseBody
    public List<User> users(HttpRequest request, HttpResponse response){
        List<User> users =  userService.getUsers();
        return users;
    }

    @RequestMapping("/user")
    @ResponseBody
    public User findUser(HttpRequest request, HttpResponse response, int id){
        User user =  userService.getUser(id);
        return user;
    }

    @RequestMapping("/userAdd")
    @ResponseBody
    public int addUser(@RequestBody User user){
        int i  =  userService.addUser(user);
        return i;
    }

    @RequestMapping("/userUpdate")
    @ResponseBody
    public int updateUser(int id, @RequestBody User user){
        int i  =  userService.updateUser(id,user);
        return i;
    }

    @RequestMapping("/userDel")
    @ResponseBody
    public String deleteUser(int id){
        int i  =  userService.deleteUser(id);
        if(i==1){
            return "成功！";
        }else {
            return "失败！";
        }
    }

    @RequestMapping("/index")
    public String index(HttpRequest request, HttpResponse response){
        response.setContentType("text/html","utf-8");
        return "index.html";
    }
}
