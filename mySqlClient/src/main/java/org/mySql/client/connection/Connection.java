package org.mySql.client.connection;

import java.io.IOException;

public interface Connection {


    Statement createStatement() throws IOException;
    Statement createStatement(String sql) throws IOException;
    void setAutoCommit(boolean autoCommit);
    boolean getAutoCommit();
    void commit();
    void rollback();
    void close();
    boolean getClosed();
}
