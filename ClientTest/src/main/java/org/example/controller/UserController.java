package org.example.controller;

import com.zxb.pojo.User;
import org.mySpring.annotation.Autowired;
import org.mySpring.annotation.Controller;
import org.mySpring.annotation.RequestBody;
import org.mySpring.annotation.ResponseBody;
import org.mySpring.web.annotation.RequestMapping;
import org.myTomcat.entity.HttpRequest;
import org.myTomcat.entity.HttpResponse;

import java.util.List;


@Controller
public class UserController {

    @Autowired
    ImportService importService;

    @RequestMapping("/users")
    @ResponseBody
    public List<User> users(HttpRequest request, HttpResponse response){
        List<User> users =  importService.getUsers();
        return users;
    }

    @RequestMapping("/user")
    @ResponseBody
    public User findUser(HttpRequest request, HttpResponse response, int id){
        User user =  importService.getUser(id);
        return user;
    }

    @RequestMapping("/userAdd")
    @ResponseBody
    public int addUser(@RequestBody User user){
        int i  =  importService.addUser(user);
        return i;
    }

    @RequestMapping("/userUpdate")
    @ResponseBody
    public int updateUser(int id, @RequestBody User user){
        int i  =  importService.updateUser(id,user);
        return i;
    }

    @RequestMapping("/userDel")
    @ResponseBody
    public String deleteUser(int id){
        int i  =  importService.deleteUser(id);
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
