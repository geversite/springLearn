package org.mySpring.boot;

import org.mySpring.context.ApplicationContext;
import org.mySpring.web.servlet.DispatcherServlet;
import org.myTomcat.core.MyTomcat;

public class MyTomcatServer implements WebServer {


    @Override
    public void start(ApplicationContext context) {
        MyTomcat myTomcat = new MyTomcat();
        int port =0;
        try{
            port = Integer.parseInt(Environment.getEnvironment().getData("server.port"));
        }catch (Exception e){
            port = 8080;
        }
        if(port==-1){
            return;
        }
        myTomcat.getConfig().setPort(port);

        myTomcat.addServlet("/*", new DispatcherServlet(context));

        try{
            myTomcat.start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
