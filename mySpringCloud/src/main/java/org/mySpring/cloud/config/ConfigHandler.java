package org.mySpring.cloud.config;

import org.mySpring.boot.Environment;
import org.myTomcat.entity.HttpRequest;
import org.myTomcat.entity.HttpResponse;

import java.util.concurrent.ConcurrentHashMap;

public class ConfigHandler {

    private final ConcurrentHashMap<String,String> configs = new ConcurrentHashMap<>();

    public Object getConfig(HttpRequest request, HttpResponse response){
        String key = request.getParam("key")[0];
        String val = configs.get(key);
        if(val == null){
            val = Environment.getEnvironment().getData(key);
        }
        return val;
    }

    public Object setConfig(HttpRequest request, HttpResponse response){
        String key = request.getParam("key")[0];
        String val = request.getParam("value")[0];
        configs.put(key,val);
        return true;
    }

    public Object registerEureka(HttpRequest request, HttpResponse response){
        String val = request.getRemoteHost();
        String key = "eurekaServer.ip";
        configs.put(key,val);
        return true;
    }

}
