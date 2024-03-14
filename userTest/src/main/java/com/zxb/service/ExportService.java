package com.zxb.service;

import org.mySpring.cloud.annotation.RPCService;

@RPCService
public class ExportService {

    public String getMsg(){
        return "hello!";
    }

    public String echo(String msg){
        return "echo~~"+msg;
    }

}
