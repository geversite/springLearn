package org.mysql.server;

import org.mylog.Logger;
import org.mysql.auth.AuthHandler;
import org.mysql.sql.SqlHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import static jdk.nashorn.internal.runtime.regexp.joni.Config.log;

public class RequestHandler implements Runnable {

    private Socket socket;
    static Logger log = Logger.getLogger();
    static AuthHandler authHandler = new AuthHandler();
    static SqlHandler sqlHandler = new SqlHandler();

    public RequestHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        boolean exit = false;
        try (
                InputStream inputStream = socket.getInputStream();
                OutputStream outputStream = socket.getOutputStream();
        ) {
            while (!exit) {
                Request request = new Request(inputStream);
                Response response = null;
                log.info("received request: " + request.getRequestType() + ": " + request.getRequestContent());
                switch (request.getRequestType()){
                    case AUTHORIZE:
                        response = authHandler.handle(request);
                        break;
                    case SQL:
                        response = sqlHandler.handle(request);
                        break;
                    case EXIT:
                        exit = true;
                        break;
                    default:
                        log.error("request type:"+request.getRequestType()+"not supported");
                        throw new Exception("request type:"+request.getRequestType()+"not supported");
                }
                if (response != null){
                    outputStream.write(response.toString().getBytes(StandardCharsets.UTF_8));
                    outputStream.flush();
                    log.info("sending response for request: " + request.getRequestType() + ": " + request.getRequestContent());
                }

            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
