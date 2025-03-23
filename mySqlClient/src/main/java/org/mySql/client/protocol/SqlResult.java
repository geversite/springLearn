package org.mySql.client.protocol;

import org.mySql.client.Exception.SqlException;
import org.mySql.client.connection.ResultSet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class SqlResult {

    InputStream stream;
    String data;


    public SqlResult(InputStream stream) {
        this.stream = stream;
    }

    public ResultSet recv() throws IOException, SqlException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        data = reader.readLine();
        return extractData(data);
    }

    public int recvUpdate() throws IOException, SqlException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        data = reader.readLine();
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
