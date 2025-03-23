package org.mySql.client.protocol;

import lombok.AllArgsConstructor;

import java.io.IOException;
import java.io.OutputStream;


public class SqlObj {

    OutputStream outputStream;
    String sql;
    String[] params;
    String[] types;

    public SqlObj(OutputStream outputStream, String sql, String[] types, Object[] params) {
        this.outputStream = outputStream;
        this.sql = sql;
        this.types = types;
        if (params != null && params.length > 0) {
            for (int i = 0; i < params.length; i++) {
                params[i] = params[i].toString();
            }
        }
    }
//[SQL][select * from user where id = ?;][PARAMS][[INT: 5]]
    public void send() throws IOException {
        StringBuilder builder = new StringBuilder();
        builder.append("[SQL]");
        builder.append("[").append(sql).append("]");
        builder.append("[PARAMS]");
        builder.append("[");
        for (int i = 0; i < params.length; i++) {
            builder.append("[").append(this.types[i]).append(":")
                    .append(params[i]).append("]");
        }
        builder.append("]");
        builder.append("[FINISH]");
        byte[] data = builder.toString().getBytes();
        outputStream.write(data);
        outputStream.flush();
    }

}
