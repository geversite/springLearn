package org.mySpring.aop;

import org.mySpring.lib.BeanLib;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PointCutParser {

    public static void parseAspect(Class<?> clazz,String beanName, Map<Method, PointCutHandler> map, Set<Class<?>> set) throws ClassNotFoundException {
        beanName = beanName.equals("")? BeanLib.getBeanName(clazz):beanName;
        for(Method method: clazz.getDeclaredMethods()){
            if(method.isAnnotationPresent(Around.class) && method.getParameterTypes()[0] == Action.class){
                String methods = method.getDeclaredAnnotation(Around.class).value();
                String[] methodPath = methods.split("\\.");
                StringBuilder builder = new StringBuilder();
                for(int i=0;i< methodPath.length-2;i++){
                    builder.append(methodPath[i]).append(".");
                }
                builder.deleteCharAt(builder.length()-1);
                if (methodPath[methodPath.length-2].equals("*")){
                    parseDir(builder.toString(), beanName, methodPath[methodPath.length-1], method, map, set);
                } else {
                    builder.append(".").append(methodPath[methodPath.length - 2]);
                    Class<?> c = Thread.currentThread().getContextClassLoader().loadClass(builder.toString());
                    if (methodPath[methodPath.length - 1].equals("*")) {
                        parseClass(c, beanName, "*", method, map ,set);
                    } else {
                        parseClass(c, beanName, methodPath[methodPath.length - 1], method, map, set);
                    }
                }
            }
        }
    }



    private static void parseClass(Class<?> clazz,String beanName, String methodName, Method proxyMethod, Map<Method, PointCutHandler> map, Set<Class<?>> set) {
        boolean matchAll = methodName.equals("*");
        for(Method method: clazz.getDeclaredMethods()){
            if(matchAll || method.getName().equals(methodName)){
                map.put(method, new PointCutHandler(beanName ,proxyMethod));
                set.add(clazz);
            }
        }
    }

    private static void parseDir(String path, String beanName, String methodName, Method proxyMethod, Map<Method, PointCutHandler> map, Set<Class<?>> set) {
        path = path.replace('.','/').trim();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        URL url = loader.getResource(path);
        File file = new File(url.getFile());
        if(!file.isDirectory()){
            System.out.println("Component is not a dir!\n");
        }else {
            for (File file1 : file.listFiles()) {
                String fileName = file1.getName();
                fileName = fileName.substring(0,fileName.lastIndexOf("."));
                fileName = (path+"."+fileName).replace('/','.');
                try {
                    Class<?> clazz = loader.loadClass(fileName);
                    parseClass(clazz,beanName, methodName, proxyMethod, map, set);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

}
