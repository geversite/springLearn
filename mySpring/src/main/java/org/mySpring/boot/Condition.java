package org.mySpring.boot;

import org.mySpring.BeanDefinition;

import java.util.Map;

public interface Condition {

    public boolean matches(Map<String, BeanDefinition> beanDefinitionDictionary);
}
