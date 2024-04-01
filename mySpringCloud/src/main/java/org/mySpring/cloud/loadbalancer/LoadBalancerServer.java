package org.mySpring.cloud.loadbalancer;

import org.myHttp.simple.SimpleServer;
import org.mySpring.cloud.config.ConfigHandler;
import org.mySpring.cloud.config.ConfigLib;

public class LoadBalancerServer{
    int port;

    public LoadBalancerServer() throws Exception {
        port = ConfigLib.loadBalancerPort();
        new Thread(new Server(port),"LoadBalancerServer").start();
    }

    public static class Server extends SimpleServer {

        public Server(int port){
            try {
                this.port = port;
                this.handler = new LoadBalancerHandler();
                registerToConfig();
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
        private void registerToConfig() {
            if(ConfigLib.isValidConfig()){
                ConfigLib.registerEureka();
            }
        }
    }
}
