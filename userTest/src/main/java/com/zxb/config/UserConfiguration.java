package com.zxb.config;

import com.zxb.pojo.User;
import org.mySpring.annotation.Bean;
import org.mySpring.annotation.Configuration;

@Configuration
public class UserConfiguration {


    @Bean
    public User getUser(){
        return new User();
    }

}
