package org.mySpring.cloud.eureka;

import org.mySpring.boot.Environment;

public class EurekaLib {

    private static Environment environment = Environment.getEnvironment();

    public static int eurekaPort(){
        int eurekaPort;
        try {
            eurekaPort = Integer.parseInt(environment.getData("eurekaServer.port"));
        } catch (Exception e) {
            eurekaPort = 8848;
        }
        return eurekaPort;
    }

    public static String eurekaIP(){
        String eurekaPort;
        try {
            eurekaPort = (environment.getData("eurekaServer.ip"));
        } catch (Exception e) {
            throw new RuntimeException("No eurekaIP Defined");
        }
        return eurekaPort;
    }

    public static int period(){
        int eurekaPort;
        try {
            eurekaPort = Integer.parseInt(environment.getData("eurekaServer.period"));
        } catch (Exception e) {
            eurekaPort = 2000;
        }
        return eurekaPort;
    }
}
