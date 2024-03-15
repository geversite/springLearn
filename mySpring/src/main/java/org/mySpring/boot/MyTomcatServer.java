package org.mySpring.boot;

import org.mySpring.context.ApplicationContext;
import org.mySpring.web.servlet.DispatcherServlet;
import org.myTomcat.config.ServerConfig;
import org.myTomcat.core.MyTomcat;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Properties;

public class MyTomcatServer implements WebServer {


    @Override
    public void start(ApplicationContext context) {
        MyTomcat myTomcat = new MyTomcat();
        ServerConfig config = envirConfig();

        if(config.getPort()==-1){
            return;
        }
        myTomcat.setConfig(config);
        myTomcat.addServlet("/*", new DispatcherServlet(context));

        try{
            myTomcat.start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private ServerConfig envirConfig() {

        ServerConfig config = new ServerConfig();
        Environment environment = Environment.getEnvironment();
        for (Field field: config.getClass().getDeclaredFields()) {
            try{
                String val = environment.getData("server."+field.getName());
                field.setAccessible(true);
                field.set(config,field.getType().getConstructor(String.class).newInstance(val));
            } catch (InvocationTargetException | IllegalAccessException | InstantiationException | NoSuchMethodException ignored) {}
        }
        return config;

    }
}
