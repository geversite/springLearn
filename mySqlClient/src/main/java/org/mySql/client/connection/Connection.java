package org.mySql.client.connection;

import org.mySql.client.Exception.SqlException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public interface Connection {


    Statement createStatement() throws IOException, SqlException;
    Statement createStatement(String sql) throws IOException, SqlException;
    void setAutoCommit(boolean autoCommit);
    boolean getAutoCommit();
    void commit() throws IOException, SqlException;
    void rollback() throws IOException, SqlException;
    void close() throws IOException;
    boolean getClosed();
    InputStream getInputStream() throws SqlException, IOException;
    OutputStream getOutputStream() throws SqlException, IOException;
}
