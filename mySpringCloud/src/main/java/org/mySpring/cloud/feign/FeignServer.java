package org.mySpring.cloud.feign;

import lombok.SneakyThrows;
import org.mySpring.context.ApplicationContext;
import org.mySpring.cloud.config.ConfigLib;
import org.myHttp.simple.HttpUtil;
import org.myHttp.simple.SimpleServer;

import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;


public class FeignServer {
    int port;

    ApplicationContext context;
    
    public FeignServer(ApplicationContext context) throws Exception {
        try {
            port = ConfigLib.feignPort();
        } catch (Exception e) {
            port = 8849;
        }
        new Thread(new Server(port,context),"FeignServer").start();
    }

    public void registerService(Class<?> clazz) {
        try {
            int eurekaPort = ConfigLib.eurekaPort();
            String serverIP = ConfigLib.eurekaIP();
            URL url = new URL("http://"+serverIP+":"+eurekaPort+"/registerService?rpcName="+clazz.getName()+"&port="+port);
            HttpUtil.doHttpGet(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class Server extends SimpleServer {
    
    
        ApplicationContext context;
    
        public Server(int port, ApplicationContext context) throws Exception{
            try {
                this.port = port;
                context.register(FeignHandler.class);
                this.handler = context.getBean(FeignHandler.class.getName());

                new Timer().schedule(new Worker(),0, ConfigLib.period()/2);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    
        @SneakyThrows
        @Override
        public void run() {
            try{
                super.run();
            }catch (Exception e){
                int eurekaPort = ConfigLib.eurekaPort();
                String serverIP = ConfigLib.eurekaIP();
                URL url = new URL("http://"+serverIP+":"+eurekaPort+"/logoffService"+"?port="+port);
                HttpUtil.doHttpGet(url);
            }
        }

        class Worker extends TimerTask {

            @SneakyThrows
            @Override
            public void run() {
                int eurekaPort = ConfigLib.eurekaPort();
                String serverIP = ConfigLib.eurekaIP();
                URL url = new URL("http://"+serverIP+":"+eurekaPort+"/renewalService"+"?port="+port);
                HttpUtil.doHttpGet(url);
            }
        }
    }
}
