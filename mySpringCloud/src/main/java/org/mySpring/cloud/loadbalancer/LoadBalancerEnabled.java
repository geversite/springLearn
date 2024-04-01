package org.mySpring.cloud.loadbalancer;

import org.mySpring.boot.Condition;
import org.mySpring.boot.SpringApplication;
import org.mySpring.cloud.annotation.LoadBalancer;
import org.mySpring.context.BeanDefinition;

import java.util.Map;

public class LoadBalancerEnabled implements Condition {
    @Override
    public boolean matches(Map<String, BeanDefinition> beanDefinitionDictionary) {
        for (Map.Entry<String, BeanDefinition> entry : beanDefinitionDictionary.entrySet()) {
            Class<?> clazz = entry.getValue().getClazz();
            if(SpringApplication.class.isAssignableFrom(clazz)){
                if (clazz.isAnnotationPresent(LoadBalancer.class)){
                    return true;
                }
            }
        }
        return false;
    }
}
