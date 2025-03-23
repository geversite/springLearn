package org.mySql.client.protocol;

import org.mySql.client.Exception.SqlException;
import org.mySql.client.connection.ResultSet;

import java.io.IOException;
import java.io.InputStream;

public class SqlResult {

    InputStream stream;
    String data;


    public SqlResult(InputStream stream) {
        this.stream = stream;
    }

    public ResultSet recv() throws IOException, SqlException {
        byte[] buffer = new byte[1024];
        StringBuilder builder = new StringBuilder();
        while (stream.read(buffer) != -1) {
            builder.append(new String(buffer));
        }
        data = builder.toString();
        return extractData(data);
    }

    public int recvUpdate() throws IOException, SqlException {
        byte[] buffer = new byte[1024];
        StringBuilder builder = new StringBuilder();
        while (stream.read(buffer) != -1) {
            builder.append(new String(buffer));
        }
        data = builder.toString();
        return extractUpdate(data);
    }
    //data: [RESULT][ERR][MSG][12312123]
    //data: [RESULT][OK][MODIFY][2]
    private int extractUpdate(String data) throws SqlException {
        return 0;
    }

    //data: [RESULT][OK][COLUMNS][[INT: id][STRING: name]][ROWS][[1,zxb][2,hhs]]
    private ResultSet extractData(String data) throws SqlException {
        return null;

    }

}
