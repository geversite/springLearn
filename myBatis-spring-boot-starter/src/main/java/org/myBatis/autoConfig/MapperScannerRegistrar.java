package org.myBatis.autoConfig;

import org.myBatis.executor.Delete;
import org.myBatis.executor.Insert;
import org.myBatis.executor.Select;
import org.myBatis.executor.Update;
import org.myBatis.configuration.Configuration;
import org.myBatis.configuration.MappedStatement;
import org.myBatis.session.SqlSession;
import org.mySpring.ApplicationContext;
import org.mySpring.BeanBuilder;
import org.mySpring.BeanDefinition;
import org.mySpring.BeanRegistrar;
import org.mySpring.lib.BeanLib;

import java.io.File;
import java.lang.reflect.*;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapperScannerRegistrar implements BeanRegistrar {

    @Override
    public Map<String, BeanDefinition> registerList(Class<?> c, ApplicationContext context) {
        List<String> basePackages = context.getBasePackages();
        Map<String, BeanDefinition> map = new HashMap<>();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();

        for (String path : basePackages) {
            path = path.replace('.','/').trim();
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
                        if(clazz.isAnnotationPresent(Mapper.class)){
                            String beanName = clazz.getAnnotation(Mapper.class).value();
                            if(beanName.equals("")){
                                beanName = BeanLib.getBeanName(clazz);
                            }
                            BeanDefinition beanDefinition = new BeanDefinition(clazz, "singleton");
                            BeanBuilder builder = new BeanBuilder();
                            builder.setBeanName("org.myBatis.session.SqlSession");
                            builder.setMethod(SqlSession.class.getMethod("getMapper",Class.class));
                            builder.setParams(new Object[]{clazz});
                            beanDefinition.setBuilder(builder);
                            map.put(beanName, beanDefinition);
                        }
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    } catch (NoSuchMethodException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return map;
    }



    private void registerMapper(Class<?> clazz, Configuration configuration) {

        for (Method method : clazz.getDeclaredMethods()) {
            MappedStatement statement = new MappedStatement();
            String id = clazz.getName() + "." + method.getName();
            statement.setId(id);
            statement.setMethod(method);
            if (method.isAnnotationPresent(Select.class)){
                String action = method.getAnnotation(Select.class).value();
                statement.setSqlMethod("select");
                statement.setSql(action);
            } else if (method.isAnnotationPresent(Update.class)) {
                String action = method.getAnnotation(Update.class).value();
                statement.setSqlMethod("update");
                statement.setSql(action);
            } else if (method.isAnnotationPresent(Insert.class)) {
                String action = method.getAnnotation(Insert.class).value();
                statement.setSqlMethod("insert");
                statement.setSql(action);
            } else if (method.isAnnotationPresent(Delete.class)) {
                String action = method.getAnnotation(Delete.class).value();
                statement.setSqlMethod("delete");
                statement.setSql(action);
            }
            configuration.getMappedStatements().put(id,statement);
        }



    }

}
