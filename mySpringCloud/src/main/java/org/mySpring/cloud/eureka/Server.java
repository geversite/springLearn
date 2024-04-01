package org.mySpring.cloud.eureka;

import org.mySpring.cloud.config.ConfigLib;
import org.mySpring.context.ApplicationContext;
import org.mySpring.web.servlet.DispatcherServlet;
import org.myTomcat.config.ServerConfig;
import org.myTomcat.core.MyTomcat;

public class Server implements Runnable{

    int port;
    ApplicationContext context;

    public Server(int port) throws Exception{
        this.port = port;
        this.context = new ApplicationContext();
    }



    @Override
    public void run() {
        try {
            context.register(ServerController.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        MyTomcat myTomcat = new MyTomcat();
        myTomcat.setConfig(new ServerConfig());
        myTomcat.getConfig().setPort(port);
        myTomcat.addServlet("/*", new DispatcherServlet(context));
        registerToConfig();

        try{
            myTomcat.start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void registerToConfig() {
        if(ConfigLib.isValidConfig()){
            ConfigLib.registerLoadBalancer();
        }
    }
}
