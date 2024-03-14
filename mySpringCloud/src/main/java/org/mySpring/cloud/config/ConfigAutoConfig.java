package org.mySpring.cloud.config;


import org.mySpring.annotation.Bean;
import org.mySpring.annotation.Configuration;
import org.mySpring.boot.Conditional;

@Configuration
public class ConfigAutoConfig {

    @Bean
    @Conditional(ServerEnabled.class)
    public ConfigServer getServer() throws Exception {
        return new ConfigServer();
    }

}
