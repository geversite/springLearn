package org.myCP;

import java.sql.Connection;

public interface IConnectionPool {

    void releaseConnection(Connection connection);
    Connection getConnection() throws InterruptedException;
}
