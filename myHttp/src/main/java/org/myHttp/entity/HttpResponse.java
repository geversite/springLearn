package org.myHttp.entity;

import lombok.Data;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Data
public class HttpResponse {

    private int statusCode = 200;
    private String reasonPhrase = "OK";
    private Map<String, String> headers = new HashMap<>();
    private StringBuilder body = new StringBuilder();

    OutputStreamWriter writer;

    public HttpResponse(Socket socket) throws IOException {
        OutputStream stream = socket.getOutputStream();
        writer= new OutputStreamWriter(stream, StandardCharsets.UTF_8);
    }

    public void setStatus(int statusCode, String reasonPhrase) {
        this.statusCode = statusCode;
        this.reasonPhrase = reasonPhrase;
    }

    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    public void writeBody(String content) {
        body.append(content);
    }

    public void sendRedirect(String path){
        setStatus(302, "Redirect");
        addHeader("Location", path);
    }

    public void writeTo(OutputStream outputStream) throws IOException {
        writer.write("HTTP/1.1 " + statusCode + " " + reasonPhrase + "\r\n");

        for (Map.Entry<String, String> header : headers.entrySet()) {
            writer.write(header.getKey() + ": " + header.getValue() + "\r\n");
        }

        if (body.length() > 0) {
            writer.write("Content-Length: " + (body.toString().getBytes(StandardCharsets.UTF_8).length) + "\r\n");
        }

        writer.write("\r\n");

        writer.write(body.toString());

        writer.flush();
    }

    public void setContentType(String s) {
        headers.put("Content-Type",s);
    }
    public void setContentType(String s, String charset) {
        headers.put("Content-Type",s+", charset="+charset);
    }
}
