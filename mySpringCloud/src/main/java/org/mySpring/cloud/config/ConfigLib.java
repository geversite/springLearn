package org.mySpring.cloud.config;

import org.mySpring.boot.Environment;
import org.myTomcat.http.HttpResponse;
import org.myTomcat.http.HttpUtil;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

public class ConfigLib {

    private static final Environment environment;

    private static final boolean validConfig ;
    private static String configIP = null;
    private static int configPort;


    static {
        environment = Environment.getEnvironment();
        validConfig= environment.getData("configServer.ip")!=null;
        if(validConfig){
            configIP = (environment.getData("configServer.ip"));
            try {
                configPort = Integer.parseInt(environment.getData("configServer.port"));
            } catch (Exception e) {
                configPort = 8847;
            }
        }    }

    public static int eurekaPort(){
        int eurekaPort;
        String key = "eurekaServer.port";

        try {
            eurekaPort = Integer.parseInt(validConfig? Objects.requireNonNull(getConfig(key)) :environment.getData(key));
        } catch (Exception e) {
            eurekaPort = 8848;
        }

        return eurekaPort;
    }

    public static String eurekaIP(){
        String eurekaIP;
        String key ="eurekaServer.ip";
        try {
            eurekaIP = validConfig?getConfig(key):environment.getData(key);
        } catch (Exception e) {
            throw new RuntimeException("No eurekaIP Defined");
        }
        return eurekaIP;
    }

    public static int feignPort(){
        int feignPort;
        String key = "feignServer.port";
        try {
            feignPort = Integer.parseInt(validConfig? Objects.requireNonNull(getConfig(key)) :environment.getData(key));
        } catch (Exception e) {
            feignPort = 8849;
        }
        return feignPort;
    }

    public static int configPort() {
        int configPort;
        String key = "configServer.port";
        try {
            configPort = Integer.parseInt(validConfig? Objects.requireNonNull(getConfig(key)) :environment.getData(key));
        } catch (Exception e) {
            configPort = 8847;
        }
        return configPort;
    }

    public static int period(){
        int period;
        String key = "eurekaServer.period";
        try {
            period = Integer.parseInt(validConfig? Objects.requireNonNull(getConfig(key)) :environment.getData(key));
        } catch (Exception e) {
            period = 20000;
        }
        return period;
    }

    private static String getConfig(String s){
        URL url = null;
        try {
            url = new URL("http://"+configIP+":"+configPort+"/getConfig"+"?key="+s);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        HttpResponse response = HttpUtil.doHttpGet(url);
        return response.getMsg();
    }

    private static String setConfig(String key, String value){

        URL url = null;
        try {
            url = new URL("http://"+configIP+":"+configPort+"/setConfig"+"?key="+key+"&value="+value);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        HttpResponse response = HttpUtil.doHttpGet(url);
        return response.getMsg();
    }

    public static String registerEureka(){
        URL url = null;
        try {
            url = new URL("http://"+configIP+":"+configPort+"/registerEureka");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        HttpResponse response = HttpUtil.doHttpGet(url);
        return response.getMsg();
    }



    public static boolean isValidConfig(){
        return validConfig;
    }


}
