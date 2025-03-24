package org.mySql.client.protocol;

import org.mySql.client.Exception.SqlException;
import org.mySql.client.connection.ResultSet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        String regex = "\\[RESULT\\]\\[(ERR|OK)\\](?:\\[MSG\\]\\[(\\d+)\\]|\\[MODIFY\\]\\[(\\d+)\\])";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(data);
        if (matcher.find()) {
            String result = matcher.group(1); // RESULT 的值
            String message = null;

            if ("ERR".equals(result)) {
                throw new SqlException(matcher.group(2)); // MSG 后的信息
            } else if ("OK".equals(result)) {
                message = matcher.group(3); // MODIFY 后的信息
            }

            return Integer.parseInt(message);
        } else {
            throw new IllegalArgumentException("Invalid data format: " + data);
        }
    }

    //data: [RESULT][OK][COLUMNS][[Integer: id][String: name]][ROWS][[1,zxb][2,hhs]]
    private ResultSet extractData(String data) throws SqlException {
        ResultSet resultSet = new ResultSet(data);
        return resultSet;

    }

}
