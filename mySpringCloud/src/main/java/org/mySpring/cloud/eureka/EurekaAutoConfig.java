package org.mySpring.cloud.eureka;

import org.mySpring.annotation.Bean;
import org.mySpring.annotation.Configuration;
import org.mySpring.boot.Conditional;
import org.mySpring.boot.Environment;
import org.mySpring.cloud.feign.FeignClientFactory;

@Configuration
public class EurekaAutoConfig {

    @Bean
    public FeignClientFactory getClientFactory(){
        return new FeignClientFactory();
    }

    @Bean
    @Conditional(ServerEnabled.class)
    public Server registerServer() throws Exception {
        int port = 0;
        try {
            port = Integer.parseInt(Environment.getEnvironment().getData("eurekaServer.port"));
        } catch (Exception e) {
            port = 8848;
        }
        Server server = new Server(port);
        new Thread(server,"EurekaServer").start();
        return server;
    }




}
