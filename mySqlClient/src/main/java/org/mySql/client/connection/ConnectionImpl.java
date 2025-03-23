package org.mySql.client.connection;


import org.mySql.client.Exception.SqlException;
import org.mySql.client.protocol.SqlObj;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class ConnectionImpl implements Connection {

    private String ip;
    private int port;
    private String user;
    protected Socket socket;
    private List<Statement> statements = new ArrayList<Statement>();
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
    public Statement createStatement() throws IOException, SqlException {
        return createStatement(null);
    }

    @Override
    public Statement createStatement(String sql) throws IOException, SqlException {
        Statement statement = new Statement(sql, this);
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
    public void commit() throws IOException, SqlException {
        createStatement("COMMIT;").executeUpdate();
        statements.clear();
    }

    @Override
    public void rollback() throws IOException, SqlException {
        createStatement("ROLLBACK;").executeUpdate();
        statements.clear();

    }

    @Override
    public void close() throws IOException {
        socket.close();
        this.closed = true;
    }

    @Override
    public boolean getClosed() {
        return this.closed;
    }

    @Override
    public InputStream getInputStream() throws SqlException, IOException {
        return this.socket.getInputStream();
    }

    @Override
    public OutputStream getOutputStream() throws SqlException, IOException {
        return this.socket.getOutputStream();
    }


}
