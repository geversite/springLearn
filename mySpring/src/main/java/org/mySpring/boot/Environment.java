package org.mySpring.boot;

import lombok.extern.java.Log;
import org.mylog.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class Environment {

    private static final Environment environment;

    public String getData(String s) {
        return data.get(s);
    }

    public Map<String, String> getData() {
        return data;
    }

    private final Map<String, String> data = new ConcurrentHashMap<>();

    private static Logger log = Logger.getLogger();

    static {
        environment=new Environment();
    }

    public static Environment getEnvironment(){
        return environment;
    }

    private Environment(){}

    protected static void environmentInit(){
        InputStream resource = Thread.currentThread().getContextClassLoader().getResourceAsStream("application.properties");
        Properties properties = new Properties();
        try {
            properties.load(resource);
            for (Map.Entry<Object, Object> entry : properties.entrySet()) {
                environment.data.put((String) entry.getKey(), (String) entry.getValue());
            }
        } catch (Exception e) {
            log.warn("Failed to parse application.properties.");
        }

    }
}
