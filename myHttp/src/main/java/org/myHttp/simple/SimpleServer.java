package org.myHttp.simple;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.myHttp.entity.HttpRequest;
import org.myHttp.entity.HttpResponse;
import org.mylog.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URISyntaxException;

@Data

public class SimpleServer implements Runnable{

    protected Object handler;

    protected int port;

    private static final Logger log = Logger.getLogger();

    public void run(){
        ServerSocket serverSocket  =null;
        Socket socket;
        try {
            serverSocket = new ServerSocket(port);
            log.info("SimpleServer started on port "+port);
            while (true){
                socket = serverSocket.accept();
                new Thread(new SimpleHandler(socket,handler)).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if(serverSocket!=null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private static class SimpleHandler implements Runnable {

        private final Socket socket;
        private final Object handler;

        private static final Logger log = Logger.getLogger();
        public SimpleHandler(Socket socket, Object handler) {
            this.socket = socket;
            this.handler = handler;
        }

        @Override
        public void run() {
            try (
                    InputStream inputStream = socket.getInputStream();
                    OutputStream outputStream = socket.getOutputStream();
            ) {
                HttpRequest request = new HttpRequest(socket);
                org.myHttp.entity.HttpResponse response = new org.myHttp.entity.HttpResponse(socket);
                log.info(request.getRequestURI());
                String uri = request.getRequestURI();
                Method method = handler.getClass().getMethod(uri.substring(1).split("\\?")[0],HttpRequest.class, HttpResponse.class);
                Object result = method.invoke(handler, request, response);
                ObjectMapper mapper = new ObjectMapper();
                String json = mapper.writeValueAsString(result);
                if(result instanceof String)
                    json = (String)result;
                response.writeBody(json);
                response.writeTo(outputStream);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
