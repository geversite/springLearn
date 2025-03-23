package org.mySql.client.connection;


import lombok.Getter;
import lombok.Setter;
import org.mySql.client.Exception.SqlException;
import org.mySql.client.protocol.SqlObj;
import org.mySql.client.protocol.SqlResult;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

public class Statement {

    Connection connection;
    OutputStream outputStream;
    InputStream inputStream;
    public static Set<String> supportedTypes = new HashSet<String>();
    @Setter @Getter
    String sql;
    String[] params = new String[]{};
    String[] types = new String[]{};

    public Statement(Connection connection) throws SqlException, IOException {
        supportedTypes.add("String");
        supportedTypes.add("Float");
        supportedTypes.add("Character");
        supportedTypes.add("Integer");
        supportedTypes.add("Double");
        supportedTypes.add("Long");
        supportedTypes.add("Boolean");
        supportedTypes.add("Short");
        supportedTypes.add("Date");
        this.connection = connection;
        this.outputStream = connection.getOutputStream();
        this.inputStream = connection.getInputStream();
    }
    public Statement(String sql, Connection connection) throws SqlException, IOException {
        this(connection);
        this.sql = sql;
    }

    public void setObject(int index, Object value) throws SqlException {
        String clazz = value.getClass().getSimpleName();
        if (!supportedTypes.contains(clazz)) {
            throw new SqlException("Parameter type not supported: " + clazz);
        }
        types[index]= clazz;
        params[index]= value.toString();
    }

    public ResultSet executeQuery() throws IOException, SqlException {
        SqlObj obj = new SqlObj(this.outputStream, sql, types, params);
        obj.send();
        SqlResult result = new SqlResult(this.inputStream);
        ResultSet resultSet = result.recv();
        if (connection.getAutoCommit()){
            connection.commit();
        }
        return resultSet;
    }

    public int executeUpdate() throws IOException, SqlException {
        SqlObj obj = new SqlObj(this.outputStream, sql, types, params);
        obj.send();
        SqlResult result = new SqlResult(this.inputStream);
        int rows = result.recvUpdate();
        if (connection.getAutoCommit()){
            connection.commit();
        }
        return rows;
    }


}
