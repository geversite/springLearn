package org.mySpring.cloud.feign;

import org.mySpring.annotation.Bean;
import org.mySpring.annotation.Configuration;
import org.mySpring.annotation.Import;
import org.mySpring.boot.Conditional;

@Configuration
@Import(FeignRegistrar.class)
public class FeignAutoConfig {

    @Bean
    public FeignClientFactory getClientFactory(){
        return new FeignClientFactory();
    }

}
