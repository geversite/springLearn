package org.myTomcat.entity;

import lombok.Data;
import org.myTomcat.lib.URIParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Data
public class HttpRequest {

    private String method;
    private String remoteHost;
    private int port;
    private String requestURI;
    private String httpVersion;
    private Map<String, String> headers = new HashMap<>();
    private String body;
    private Map<String, String[]> parameterMap = new HashMap<>();
    private BufferedReader reader;

    public HttpRequest(Socket socket) throws IOException, URISyntaxException {
        InputStream inputStream = socket.getInputStream();
        remoteHost = socket.getInetAddress().getHostAddress();
        port = socket.getPort();
        reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        String line;

        line = reader.readLine();
        if (line != null) {
            String[] requestLineParts = URLDecoder.decode(line, "UTF-8").split(" ");
            this.method = requestLineParts[0];
            this.requestURI = requestLineParts[1];
            this.httpVersion = requestLineParts[2];
        }

        parameterMap = URIParser.parseURI(requestURI);

        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            String[] headerParts = line.split(": ");
            if (headerParts.length == 2) {
                headers.put(headerParts[0], headerParts[1]);
            }
        }

        if (headers.containsKey("Content-Length")) {
            int contentLength = Integer.parseInt(headers.get("Content-Length"));
            char[] bodyChars = new char[contentLength];
            reader.read(bodyChars, 0, contentLength);
            this.body = new String(bodyChars);
        }
    }

    public String[] getParam(String key){
        return parameterMap.get(key);
    }

}
