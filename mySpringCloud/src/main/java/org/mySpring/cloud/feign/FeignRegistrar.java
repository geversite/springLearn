package org.mySpring.cloud.feign;

import org.mySpring.annotation.Import;
import org.mySpring.cloud.annotation.FeignClient;
import org.mySpring.context.ApplicationContext;
import org.mySpring.context.BeanBuilder;
import org.mySpring.context.BeanDefinition;
import org.mySpring.context.BeanRegistrar;
import org.mySpring.cloud.annotation.RPCService;
import org.mylog.Logger;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Import(InjectionProcessor.class)
public class FeignRegistrar implements BeanRegistrar {

    Logger log = Logger.getLogger();
    @Override
    public Map<String, BeanDefinition> registerList(Class<?> config, ApplicationContext context) throws Exception {
        List<String> basePackages = context.getBasePackages();
        Map<String, BeanDefinition> map = new HashMap<>();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        boolean registered = false;
        FeignServer feignServer=null;
        for (String path : basePackages) {
            path = path.replace('\\','/').trim();
            URL url = loader.getResource(path);
            File file = new File(url.getFile());
            if(!file.isDirectory()){
                log.warn("Component is not a dir!\n");
            }else {
                for (File file1 : file.listFiles()) {
                    String fileName = file1.getName();
                    fileName = fileName.substring(0,fileName.lastIndexOf("."));
                    fileName = (path+"."+fileName).replace('/','.');
                    try {
                        Class<?> clazz = loader.loadClass(fileName);
                        if(clazz.isAnnotationPresent(RPCService.class)) {
                            map.put(clazz.getName(), new BeanDefinition(clazz, "singleton"));
                            if(!registered){
                                feignServer = new FeignServer(context);
                                registered = true;
                            }
                            feignServer.registerService(clazz);
                        }else if(clazz.isAnnotationPresent(FeignClient.class)) {
                            BeanDefinition beanDefinition = new BeanDefinition(clazz, "singleton");
                            BeanBuilder builder = new BeanBuilder();
                            builder.setBeanName(FeignClientFactory.class.getName());
                            builder.setMethod(FeignClientFactory.class.getMethod("build", Class.class, String.class));
                            builder.setParams(new Object[]{clazz, clazz.getAnnotation(FeignClient.class).value()});
                            beanDefinition.setBuilder(builder);
                            map.put(clazz.getName(), beanDefinition);
                        }
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return map;
    }


}
