package org.myCP;


import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;


public class DataSource implements javax.sql.DataSource {

    DataSourceConfig config ;
    ConnectionPool connectionPool;



    public DataSource(){
        this(DataSourceConfig.defaultConfig);
    }

    public DataSource(DataSourceConfig config){
        this.config = config;
        connectionPool = new ConnectionPool(config, this);
    }

    public Connection getConnection() {
        try {
            return connectionPool.getConnection();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void close(Connection connection){
        connectionPool.releaseConnection(connection);
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return null;
    }


    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return null;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {

    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {

    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }


}
