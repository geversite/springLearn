package org.mySql.client.connection;

import org.mySql.client.Exception.SqlException;

import java.io.IOException;
import java.net.Socket;

public class DriverManager {
    public static Connection getConnection(String url, String user, String password) throws IOException, SqlException {
        String host = url.split("/")[0];
        String ip = host.split(":")[0];
        String port = host.split(":")[1];
        String database = url.split("/")[1];
        Connection conn = new ConnectionImpl(ip, port, database);
        if(authorize(conn, user, password)){
            return conn;
        }
        throw new SqlException("Authorize failed");
    }

    private static boolean authorize(Connection conn, String user, String password) {
        return true;
    }

}
