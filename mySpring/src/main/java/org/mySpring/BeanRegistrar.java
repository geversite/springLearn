package org.mySpring;

import java.util.Map;

public interface BeanRegistrar {
    Map<String, BeanDefinition> registerList(Class<?> config, ApplicationContext context) throws Exception;

}
