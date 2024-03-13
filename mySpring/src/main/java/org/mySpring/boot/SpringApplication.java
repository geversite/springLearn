package org.mySpring.boot;


import org.mySpring.ApplicationContext;

import java.util.Map;

public class SpringApplication {

    public static void run(Class<?> clazz) throws Exception {
        Environment.environmentInit();
        ApplicationContext context = new ApplicationContext(clazz);
        getWebServer(context).start(context);
    }

    private static WebServer getWebServer(ApplicationContext context) throws IllegalAccessException {
        Map<String,WebServer> map = context.getBeansOfType(WebServer.class);

        if(map.size()==0){
            throw new NullPointerException();
        }
        if(map.size()>1){
            throw new IllegalStateException();
        }
        return map.values().stream().findFirst().get();
    }


}
