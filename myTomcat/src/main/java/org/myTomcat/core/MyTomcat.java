package org.myTomcat.core;

import org.mylog.Logger;
import org.myTomcat.config.ServerConfig;
import org.myTomcat.entity.HttpServlet;
import org.myTomcat.lib.ServerParser;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;



public class MyTomcat {

    static Logger log = Logger.getLogger();
    ServerSocket serverSocket = null;


    Map<String, HttpServlet> servletMapping = new HashMap<>();



    private ServerConfig config;

    public MyTomcat(){
        init();
    }

    public MyTomcat(int port){
        this();
        this.config.setPort(port);
    }

    private void init() {
        config = ServerParser.getConfig();
        ServerParser.initServlets(servletMapping);
    }


    public void start() throws Exception{
        Long startingTime = System.currentTimeMillis();
        log.info("MyTomcat Starting...");

        if (config==null){
            config = new ServerConfig();
            config.setPort(8088);
        }

        for (HttpServlet servlet : servletMapping.values()) {
            servlet.init();
        }

        Socket socket = null;
        try {
            serverSocket = new ServerSocket(config.getPort());
            Long finTime = System.currentTimeMillis();
            log.info("MyTomcat Started on port "+config.getPort()+" in "+(finTime-startingTime)+"ms...");
            while (true){
                socket = serverSocket.accept();
                new Thread(new HandlerRequest(socket,servletMapping)).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if(serverSocket!=null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void addServlet(String pattern, HttpServlet servlet){
        servletMapping.put(pattern,servlet);
    }

    public ServerConfig getConfig() {
        return config;
    }

    public void setConfig(ServerConfig config) {
        this.config = config;
    }

}
