package org.mySpring.cloud.feign;

import org.mySpring.annotation.ApplicationContextAware;
import org.mySpring.annotation.Component;
import org.mySpring.cloud.annotation.FeignClient;
import org.mySpring.context.ApplicationContext;
import org.mySpring.context.BeanPostProcessor;

import java.lang.reflect.Field;

@Component
public class InjectionProcessor implements BeanPostProcessor, ApplicationContextAware {

    ApplicationContext context;
    @Override
    public Object postProcessBeforeInitialization(String beanName, Object instance) {
        return instance;
    }

    @Override
    public Object postProcessAfterInitialization(String beanName, Object instance) {
        Class<?> clazz = instance.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            if(field.isAnnotationPresent(FeignClient.class)){
                Class<?> fieldType = field.getType();
                Object built = context.getBean(FeignClientFactory.class.getName(), FeignClientFactory.class).build(fieldType,fieldType.getName());
                field.setAccessible(true);
                try {
                    field.set(instance, built);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return instance;
    }

    @Override
    public void setApplicationContext(ApplicationContext context) {
        this.context = context;
    }
}
