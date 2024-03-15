package org.myTomcat.core;

import org.mylog.Logger;
import org.myTomcat.entity.HttpRequest;
import org.myTomcat.entity.HttpResponse;
import org.myTomcat.entity.HttpServlet;
import org.myTomcat.lib.PathParser;

import java.io.*;
import java.net.Socket;
import java.net.URISyntaxException;
import java.util.Map;

public class HandlerRequest implements Runnable{

    Socket socket;

    Map<String, HttpServlet> map;

    static Logger log = Logger.getLogger();

    public HandlerRequest(Socket socket, Map<String, HttpServlet> map){
        this.map = map;
        this.socket = socket;
    }

    @Override
    public void run(){
        try (
                InputStream inputStream =  socket.getInputStream();
                OutputStream outputStream = socket.getOutputStream();
             ) {
            HttpRequest request = new HttpRequest(socket);
            log.info("received request with uri "+request.getRequestURI());
            HttpResponse response = new HttpResponse(socket);
            HttpServlet servlet = null;
            for (String s : map.keySet()) {
                if(PathParser.match(request.getRequestURI(), s)){
                    if(servlet!=null){
                        log.warn("MyTomcat: uri "+request.getRequestURI()+"has more than 1 matches, only the latter will be kept.");
                    }
                    servlet = map.get(s);
                }
            }
            if(servlet==null){
                try (InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(request.getRequestURI().substring(1))) {
                    if(stream==null){
                        response.setStatus(404, "Not Found");
                        response.addHeader("Content-Type", "text/html; charset=UTF-8");
                        response.writeTo(outputStream);
                        return;
                    }
                    StringBuilder builder = new StringBuilder();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                    }
                    response.writeBody(builder.toString());
                } catch (Exception e){
                    response.setStatus(404, "Not Found");
                    response.addHeader("Content-Type", "text/plain; charset=UTF-8");
                    response.writeTo(outputStream);
                    return;
                }

            }
            if(request.getMethod().equals("GET")){
                servlet.doGet(request,response);
            } else if (request.getMethod().equals("POST")) {
                servlet.doPost(request,response);
            }
            response.writeTo(outputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

}
