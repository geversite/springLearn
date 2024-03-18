package org.mySpring.cloud.config;

import org.myHttp.simple.SimpleServer;

public class ConfigServer {
    int port;

    public ConfigServer() throws Exception {
        port = ConfigLib.configPort();
        new Thread(new org.mySpring.cloud.config.ConfigServer.Server(port),"ConfigServer").start();
    }

    public static class Server extends SimpleServer {

        public Server(int port){
            try {
                this.port = port;
                this.handler = new ConfigHandler();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void run() {
            try{
                super.run();
            }catch (Exception e){
                throw new RuntimeException();
            }
        }

    }
}
