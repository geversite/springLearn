package org.myHttp.entity;

import lombok.Data;
import org.myHttp.config.ServletConfig;

@Data
public class HttpServlet {
    
    protected ServletConfig servletConfig;

    public void init() {
    }

    public void doGet(HttpRequest request, HttpResponse response){

    }

    public void doPost(HttpRequest request, HttpResponse response){

    }
}
