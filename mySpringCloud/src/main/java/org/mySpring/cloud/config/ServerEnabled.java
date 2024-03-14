package org.mySpring.cloud.config;

import org.mySpring.boot.Condition;
import org.mySpring.boot.SpringApplication;
import org.mySpring.cloud.annotation.ConfigServer;
import org.mySpring.context.BeanDefinition;

import java.util.Map;

public class ServerEnabled implements Condition {

    @Override
    public boolean matches(Map<String, BeanDefinition> beanDefinitionDictionary) {
        for (Map.Entry<String, BeanDefinition> entry : beanDefinitionDictionary.entrySet()) {
            Class<?> clazz = entry.getValue().getClazz();
            if(SpringApplication.class.isAssignableFrom(clazz)){
                if (clazz.isAnnotationPresent(ConfigServer.class)){
                    return true;
                }
            }
        }
        return false;
    }
}
