package org.mySpring.cloud.eureka;

import org.mySpring.ApplicationContext;
import org.mySpring.web.servlet.DispatcherServlet;
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

        myTomcat.getConfig().setPort(port);

        myTomcat.addServlet("/*", new DispatcherServlet(context));

        try{
            myTomcat.start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
