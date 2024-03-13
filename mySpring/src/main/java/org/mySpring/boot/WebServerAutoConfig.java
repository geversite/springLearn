package org.mySpring.boot;

import org.mySpring.annotation.Bean;
import org.mySpring.annotation.Configuration;


@Configuration
public class WebServerAutoConfig {

    @Bean
    @ConditionalOnClass("org.myTomcat.core.MyTomcat")
    public MyTomcatServer getMyTomcat(){
        return new MyTomcatServer();
    }


    @Bean
    @ConditionalOnClass("org.apache.catalina.startup.Tomcat")
    public TomcatWebServer getTomcat(){
        return new TomcatWebServer();
    }

    @Bean
    @ConditionalOnClass("org.eclipse.jetty.server.Server")
    public JettyWebServer getJetty(){
        return new JettyWebServer();
    }


}
