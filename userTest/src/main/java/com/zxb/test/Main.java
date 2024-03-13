package com.zxb.test;

import com.zxb.mapper.UserMapper;
import com.zxb.pojo.User;
import org.myBatis.io.Resources;
import org.myBatis.session.SqlSession;
import org.myBatis.session.SqlSessionFactoryBuilder;

import java.io.InputStream;

// 按两次 ⇧ 打开“随处搜索”对话框并输入 `show whitespaces`，
// 然后按 Enter 键。现在，您可以在代码中看到空格字符。
public class Main {
    public static void main(String[] args) throws Exception {
        String url = "mybatis-config.xml";
        InputStream stream = Resources.getResourceAsStream(url);
        SqlSession session = new SqlSessionFactoryBuilder().build(stream).openSession();
        UserMapper mapper = session.getMapper(UserMapper.class);
        System.out.println(mapper.getUsers());
        System.out.println(mapper.getUserById(1));
        User user =new User();
        user.setName("newName!");
        System.out.println(mapper.updateUser(2,user));

    }
}