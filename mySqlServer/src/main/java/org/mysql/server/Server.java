package org.mysql.server;


import org.mylog.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Server {

    static Logger log = Logger.getLogger();
    ServerSocket serverSocket = null;

    ThreadPoolExecutor pool;
    private ServerConfig config;

    public Server(){
        init();
    }

    private void init() {
        config = ServerParser.getConfig();
    }

    public void start() {
        Long startingTime = System.currentTimeMillis();
        log.info("MySql Starting...");
        if (config==null){
            config = new ServerConfig();
        }
        pool = new ThreadPoolExecutor(config.getThread(), config.getMaxThread(), 60, TimeUnit.SECONDS,new LinkedBlockingQueue<>());
        Socket socket = null;
        try {
            serverSocket = new ServerSocket(config.getPort());
            Long finTime = System.currentTimeMillis();
            log.info("MySql Started on port "+config.getPort()+" in "+(finTime-startingTime)+"ms...");
            while (true){
                socket = serverSocket.accept();
                pool.execute(new RequestHandler(socket));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if(serverSocket!=null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
