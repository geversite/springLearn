package org.mySql.client.connection;


import org.mySql.client.protocol.SqlObj;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;


public class ConnectionImpl implements Connection {

    private String ip;
    private int port;
    private String user;
    private Socket socket;
    private List<Statement> statements;
    private boolean autoCommit;
    private boolean closed = false;

    protected ConnectionImpl(String ip, String port, String database) throws IOException {
        this.ip = ip;
        this.port = Integer.parseInt(port);
        this.user = database;
        this.socket = new Socket(this.ip, this.port);
        socket.setKeepAlive(true);
    }

    @Override
    public Statement createStatement() throws IOException {
        Statement statement = new Statement(this, this.socket.getOutputStream(),this.socket.getInputStream());
        statements.add(statement);
        return statement;
    }

    @Override
    public Statement createStatement(String sql) throws IOException {
        Statement statement = new Statement(sql, this, this.socket.getOutputStream(), this.socket.getInputStream());
        statements.add(statement);
        return statement;
    }

    @Override
    public void setAutoCommit(boolean autoCommit) {
        this.autoCommit = autoCommit;
    }

    @Override
    public boolean getAutoCommit() {
        return this.autoCommit;
    }


    @Override
    public void commit() {

    }

    @Override
    public void rollback() {

    }

    @Override
    public void close() {
        this.closed = true;
    }

    @Override
    public boolean getClosed() {
        return this.closed;
    }






}
