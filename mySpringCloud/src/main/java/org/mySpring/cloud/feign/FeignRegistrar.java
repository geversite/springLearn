package org.mySpring.cloud.feign;

import org.mySpring.ApplicationContext;
import org.mySpring.BeanBuilder;
import org.mySpring.BeanDefinition;
import org.mySpring.BeanRegistrar;
import org.mySpring.cloud.RPCService;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeignRegistrar implements BeanRegistrar {


    @Override
    public Map<String, BeanDefinition> registerList(Class<?> config, ApplicationContext context) throws Exception {
        List<String> basePackages = context.getBasePackages();
        Map<String, BeanDefinition> map = new HashMap<>();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        boolean registered = false;
        FeignServer feignServer=null;
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
                        if(clazz.isAnnotationPresent(RPCService.class)) {
                            String beanName = clazz.getAnnotation(RPCService.class).value();
                            if (beanName.equals("")) {
                                map.put(clazz.getName(), new BeanDefinition(clazz, "singleton"));
                                if(!registered){
                                    feignServer = new FeignServer(context);
                                    registered = true;
                                }
                                feignServer.registerService(clazz);
                            }else {
                                BeanDefinition beanDefinition = new BeanDefinition(clazz, "singleton");
                                BeanBuilder builder = new BeanBuilder();
                                builder.setBeanName("org.mySpring.cloud.feign.FeignClientFactory");
                                builder.setMethod(FeignClientFactory.class.getMethod("build", Class.class));
                                builder.setParams(new Object[]{clazz});
                                beanDefinition.setBuilder(builder);
                                map.put(clazz.getName(), beanDefinition);
                            }
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
