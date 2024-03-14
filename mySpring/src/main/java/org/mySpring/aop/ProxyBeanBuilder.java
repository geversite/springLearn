package org.mySpring.aop;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.mySpring.context.ApplicationContext;
import org.mySpring.context.BeanDefinition;

import java.lang.reflect.Method;
import java.util.Map;

public class ProxyBeanBuilder {

    public static Object build(BeanDefinition definition, Map<Method, PointCutHandler> map, ApplicationContext context){
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(definition.getClazz());
        enhancer.setCallback(new MethodInterceptor() {
            @Override
            public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                if(map.containsKey(method)){
                    Object caller = context.getBean(map.get(method).getCallerName());
                    Action action = new Action(methodProxy, o, objects);
                    return map.get(method).getMethod().invoke(caller, action);
                }
                return methodProxy.invokeSuper(o, objects);
            }
        });
        return enhancer.create();
    }
}
