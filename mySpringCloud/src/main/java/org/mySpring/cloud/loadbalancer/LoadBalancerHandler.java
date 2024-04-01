package org.mySpring.cloud.loadbalancer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.myHttp.entity.HttpRequest;
import org.myHttp.entity.HttpResponse;
import org.myHttp.simple.HttpUtil;
import org.mySpring.cloud.config.ConfigLib;
import org.mylog.Logger;

import java.net.URL;
import java.util.*;

public class LoadBalancerHandler {

    private final Map<String, Balancer> map = new HashMap<>();
    Logger log = Logger.getLogger();

    public LoadBalancerHandler(){
        new Timer().schedule(new Worker(),0, ConfigLib.loadBalancerPeriod());
    }

    public String getService(HttpRequest request, HttpResponse response){
        String rpcName = request.getParam("rpcName")[0];
        return map.get(rpcName).next();
    }

    class Worker extends TimerTask {

        @SneakyThrows
        @Override
        public void run() {
            URL url =new URL("http://"+ConfigLib.eurekaIP()+":"+ConfigLib.eurekaPort()+"/getServices");
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String,List<String>> newMap = objectMapper.readValue(HttpUtil.doHttpGet(url).getMsg(), Map.class);
            for(String rpcName: newMap.keySet()){
                if(map.containsKey(rpcName) && newMap.get(rpcName).equals(map.get(rpcName).getIPs())){
                    continue;
                }
                map.put(rpcName,new SimpleBalancer(newMap.get(rpcName)));
            }
        }
    }
}
