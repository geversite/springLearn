package org.mySpring.cloud.feign;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.mySpring.boot.Environment;
import org.mySpring.cloud.RPCService;
import org.mySpring.cloud.eureka.EurekaLib;
import org.mySpring.lib.TypeSwitch;
import org.myTomcat.http.HttpResponse;
import org.myTomcat.http.HttpUtil;

import java.lang.reflect.Method;
import java.net.URL;

public class FeignClientFactory {

    public Object build(Class<?> clazz){
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clazz);
        enhancer.setCallback(new MethodInterceptor() {
            @Override
            public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                String serverClass = clazz.getAnnotation(RPCService.class).value();
                Environment environment = Environment.getEnvironment();
                int port;
                try {
                    port = Integer.parseInt(environment.getData("eurekaServer.port"));
                } catch (Exception e) {
                    port = 8848;
                }
                String serverIP = EurekaLib.eurekaIP();
                // 创建一个URL对象
                URL url = new URL("http://"+serverIP+":"+port+"/getService?rpcName="+clazz.getAnnotation(RPCService.class).value());
                // 打开连接
                HttpResponse response = HttpUtil.doHttpGet(url);
                String remoteHost = response.getMsg();
                String str2Service = "http://"+remoteHost+"/doService?className="+serverClass+"&method="+method.getName();
                for (Class<?> parameterType : method.getParameterTypes()) {
                    str2Service = str2Service + "&parameterType="+ parameterType.getName();
                }
                URL url2Service = new URL(str2Service);
                String json = new ObjectMapper().writeValueAsString(objects);
                json = HttpUtil.doHttpPost(url2Service,json).getMsg();
                Object oo = new ObjectMapper().readValue(json, method.getReturnType());

                return oo;
            }
        });
        return enhancer.create();
    }
}
