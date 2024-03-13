package org.mySpring.boot;


import org.mySpring.ApplicationContext;

public class JettyWebServer implements WebServer{


    @Override
    public void start(ApplicationContext context1) {
        System.out.println("starting jetty...");

    }
}
