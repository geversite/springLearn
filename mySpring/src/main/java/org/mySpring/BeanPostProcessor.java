package org.mySpring;

public interface BeanPostProcessor {

    Object postProcessBeforeInitialization(String beanName, Object instance);

    Object postProcessAfterInitialization(String beanName, Object instance);

}
