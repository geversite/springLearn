package org.myTomcat.autoConfig;

import org.mySpring.annotation.Bean;
import org.mySpring.annotation.Configuration;
import org.mySpring.boot.ConditionalOnClass;
import org.mySpring.boot.WebServer;

@Configuration
public class AutoConfig {

    @Bean
    @ConditionalOnClass("org.myTomcat.core.MyTomcat")
    public WebServer getMyTomcat(){
        return new MyTomcatServer();
    }
}
