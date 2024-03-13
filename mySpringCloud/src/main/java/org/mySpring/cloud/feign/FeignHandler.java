package org.mySpring.cloud.feign;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.mySpring.ApplicationContext;
import org.mySpring.annotation.*;
import org.mySpring.lib.TypeSwitch;
import org.myTomcat.entity.HttpRequest;
import org.myTomcat.entity.HttpResponse;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Component
public class FeignHandler implements ApplicationContextAware {

    ApplicationContext context =null;

    public Object doService(HttpRequest request, HttpResponse response) throws IOException, ClassNotFoundException {
        try {
            String className = request.getParam("className")[0];
            String method = request.getParam("method")[0];
            String[] types = request.getParam("parameterType");
            types = types==null?new String[0]:types;
            Class<?>[] classTypes = new Class<?>[types.length];
            for (int i = 0; i < classTypes.length; i++) {
                classTypes[i] = TypeSwitch.loadClass(types[i]);
            }
            ObjectMapper mapper = new ObjectMapper();
            Object[] args = DeSerializer.deserializeParams(request.getBody(), types, mapper);
            Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
            Object obj = context.getBean(className);
            Method clazzMethod = clazz.getMethod(method, classTypes);
            return clazzMethod.invoke(obj, args);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void setApplicationContext(ApplicationContext context) {
        this.context = context;
    }
}
