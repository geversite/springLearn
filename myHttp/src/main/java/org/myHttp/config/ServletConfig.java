package org.myHttp.config;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class ServletConfig {
    String servletName;
    String urlPattern;
    Map<String,String> initParams = new HashMap<>();

    public String getInitParameter(String s){
        return initParams.get(s);
    }
}
