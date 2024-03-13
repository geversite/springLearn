package com.zxb.mapper;


import com.zxb.pojo.User;
import org.myBatis.autoConfig.Mapper;
import org.myBatis.executor.*;

import java.util.List;

@Mapper
public interface UserMapper {

    @Select("select * from user_info")
    List<User> getUsers();

    @Select("select * from user_info where id = #{id}")
    User getUserById(@Param("id") int id);

    @Update("update user_info set name = #{selectedUser.name} where id = #{theId}")
    int updateUser(int theId, @Param("selectedUser") User user);

    @Delete("delete from user_info where id = #{id}")
    int deleteUserById(int id);

    @Insert("insert into user_info values(0, #{name}, #{user.passwd})")
    int insertUser(User user);
}
