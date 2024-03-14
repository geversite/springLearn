package org.mySpring.cloud.eureka;

import org.mySpring.annotation.Bean;
import org.mySpring.annotation.Configuration;
import org.mySpring.boot.Conditional;
import org.mySpring.cloud.config.ConfigLib;

@Configuration
public class EurekaAutoConfig {


    @Bean
    @Conditional(ServerEnabled.class)
    public Server registerServer() throws Exception {
        int port = ConfigLib.eurekaPort();
        Server server = new Server(port);
        new Thread(server,"EurekaServer").start();
        return server;
    }




}
