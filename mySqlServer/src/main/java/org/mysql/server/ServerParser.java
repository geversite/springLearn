package org.mysql.server;

import org.mylog.Logger;

import java.io.InputStream;
import java.util.Properties;

public class ServerParser {

    static Logger log = Logger.getLogger();

    public static ServerConfig getConfig(){
        try {
            InputStream stream = ServerConfig.class.getClassLoader().getResourceAsStream("applications.properties");
            Properties properties = new Properties();
            properties.load(stream);

            ServerConfig config = new ServerConfig();

            // 读取端口号
            String portStr = properties.getProperty("server.port");
            if (portStr != null) {
                Integer port = Integer.valueOf(portStr);
                config.setPort(port);
            }

            // 读取线程数
            String threadStr = properties.getProperty("server.thread");
            if (threadStr != null) {
                Integer thread = Integer.valueOf(threadStr);
                config.setThread(thread);
            }

            // 读取最大线程数
            String maxThreadStr = properties.getProperty("server.maxThread");
            if (maxThreadStr != null) {
                Integer maxThread = Integer.valueOf(maxThreadStr);
                config.setMaxThread(maxThread);
            }

            return config;
        } catch (Exception e) {
            log.warn("applications.properties not found or illegal");
        }
        return null;
    }

}
