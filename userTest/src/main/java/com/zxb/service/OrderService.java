package com.zxb.service;

import org.mySpring.annotation.Autowired;
import org.mySpring.annotation.Service;

@Service
public class OrderService {

    @Autowired
    UserService userService;
}
