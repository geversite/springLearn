package org.myTomcat.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.myTomcat.core.HandlerRequest;
import org.myTomcat.entity.HttpRequest;
import org.myTomcat.entity.HttpResponse;
import org.myTomcat.entity.HttpServlet;

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

    public void run(){
        ServerSocket serverSocket  =null;
        Socket socket;
        try {
            serverSocket = new ServerSocket(port);
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
                HttpResponse response = new HttpResponse(socket);
                String uri = request.getRequestURI();
                Method method = handler.getClass().getMethod(uri.substring(1).split("\\?")[0],HttpRequest.class, HttpResponse.class);
                Object result = method.invoke(handler, request, response);
                ObjectMapper mapper = new ObjectMapper();
                String json = mapper.writeValueAsString(result);
                if(method.getReturnType()==String.class)
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
