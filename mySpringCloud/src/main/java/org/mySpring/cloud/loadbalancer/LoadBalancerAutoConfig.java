package org.mySpring.cloud.loadbalancer;

import org.mySpring.annotation.Bean;
import org.mySpring.annotation.Configuration;
import org.mySpring.boot.Conditional;

@Configuration
public class LoadBalancerAutoConfig {

    @Bean
    @Conditional(LoadBalancerEnabled.class)
    public LoadBalancerServer getServer() throws Exception {
        return new LoadBalancerServer();
    }
}
