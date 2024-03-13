package org.myBatis.autoConfig;

import org.mySpring.BeanDefinition;
import org.mySpring.boot.Condition;

import java.util.Map;

public class EnableMybatis implements Condition {
    @Override
    public boolean matches(Map<String, BeanDefinition> beanDefinitionDictionary) {
        for (BeanDefinition value : beanDefinitionDictionary.values()) {
            if(value.getClazz().isAnnotationPresent(Mapper.class)){
                return true;
            }
        }
        return false;
    }
}
