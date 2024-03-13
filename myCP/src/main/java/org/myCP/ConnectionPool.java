package org.myCP;

import lombok.SneakyThrows;
import org.mylog.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class ConnectionPool implements IConnectionPool {

    private DataSourceConfig config;

    private DataSource dataSource;

    private AtomicInteger connectionCnt = new AtomicInteger(0);

    private Queue<Connection> freeQueue = new ConcurrentLinkedQueue<>();

    private Queue<Connection> busyQueue = new ConcurrentLinkedQueue<>();

    private Map<Connection,Long> lastLock = new ConcurrentHashMap<>();

    private Logger log = Logger.getLogger();

    public ConnectionPool(DataSourceConfig config, DataSource dataSource) {
        this.dataSource = dataSource;
        this.config = config;
        init();
    }

    private void init() {
        for (int i = 0; i < config.getInitSize(); i++) {
            Connection connection = createConnection();
            freeQueue.add(connection);
        }
        if(config.getHealth()){
            new Timer().schedule(new Worker(),config.getDelay().longValue(),config.getPeriod().longValue());
        }

    }

    class Worker extends TimerTask{

        @SneakyThrows
        @Override
        public void run() {
            log.debug("--------Routine Check------------");
            for (Map.Entry<Connection, Long> e : lastLock.entrySet()) {
                Connection connection = e.getKey();
                if(System.currentTimeMillis()-e.getValue()>config.getTimeout()){
                    if(isAvailable(connection)){
                        log.info("--------Routine Check:"+connection+"time out------------");
                        ((ConnectionImpl)connection).forceClose();
                        releaseConnection(connection);
                        connectionCnt.decrementAndGet();
                    }
                }
            }
        }
    }

    private boolean isAvailable(Connection connection){
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Connection createConnection() {
        try {
            Class.forName(config.getDriver());
            connectionCnt.incrementAndGet();
            Connection connection = new ConnectionImpl(DriverManager.getConnection(config.getUrl(), config.getUsername(), config.getPassword()),dataSource);
            log.debug("Connection "+connection+" Created by "+Thread.currentThread().getName()+"! cnt:"+connectionCnt.get()+" freeCnt:"+freeQueue.size()+"  busyCnt:"+busyQueue.size());
            return connection;
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public synchronized Connection getConnection() throws InterruptedException {
        Connection connection;
        if(connectionCnt.get()<config.getInitSize()){
             connection = createConnection();
        }else {
             connection = freeQueue.poll();
        }
        if (isAvailable(connection)){
            busyQueue.add(connection);
            lastLock.put(connection,System.currentTimeMillis());
        }else {
            if (connection == null) {
                if (connectionCnt.get() < config.getMaxSize()) {
                    connection = createConnection();
                    lastLock.put(connection,System.currentTimeMillis());
                    busyQueue.add(connection);
                    log.debug("Connection "+connection+" got by "+Thread.currentThread().getName()+"! cnt:"+connectionCnt.get()+" freeCnt:"+freeQueue.size()+"  busyCnt:"+busyQueue.size());
                } else {
                    this.wait(config.getWaittime());
                    log.debug(Thread.currentThread().getName()+" weaken! cnt:"+connectionCnt.get()+" freeCnt:"+freeQueue.size()+"  busyCnt:"+busyQueue.size());
                    connection = getConnection();
                }
            }else {
                connectionCnt.decrementAndGet();
            }
        }
        return connection;
    }


    @Override
    public void releaseConnection(Connection connection) {
        busyQueue.remove(connection);
        lastLock.remove(connection);
        if(isAvailable(connection)){
            freeQueue.add(connection);
        }else {
            connectionCnt.decrementAndGet();
        }
        log.debug("Connection "+connection+" released by "+Thread.currentThread().getName()+"! cnt:"+connectionCnt.get()+" freeCnt:"+freeQueue.size()+"  busyCnt:"+busyQueue.size());
        synchronized (this){
            this.notify();
        }
    }

}

