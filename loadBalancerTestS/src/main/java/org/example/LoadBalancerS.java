package org.example;

import org.mySpring.boot.SpringApplication;
import org.mySpring.boot.SpringBootApplication;
import org.mySpring.cloud.annotation.LoadBalancer;


@LoadBalancer
@SpringBootApplication
public class LoadBalancerS extends SpringApplication {
    public static void main(String[] args) throws Exception {
        run(LoadBalancerS.class);
    }
}
