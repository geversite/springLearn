package org.myTomcat.lib;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.mylog.Logger;
import org.myTomcat.config.ServerConfig;
import org.myHttp.config.ServletConfig;
import org.myHttp.entity.HttpServlet;

import java.io.*;
import java.util.List;
import java.util.Map;

public class ServerParser {

    static Logger log = Logger.getLogger();

    public static ServerConfig getConfig(){
        try {
            InputStream stream = ServerParser.class.getClassLoader().getResourceAsStream("server.xml");
            SAXReader reader = new SAXReader();
            Document document = reader.read(stream);
            ServerConfig config = new ServerConfig();
            Element root = document.getRootElement();
            Element element = root.element("port");
            Integer port = Integer.valueOf(element.getTextTrim());
            config.setPort(port);
            element = root.element("thread");
            Integer thread = Integer.valueOf(element.getTextTrim());
            config.setThread(thread);
            element = root.element("maxThread");
            Integer maxThread = Integer.valueOf(element.getTextTrim());
            config.setMaxThread(maxThread);
            return config;
        }catch (Exception e){
            log.warn("server.xml not found or illegal");
        }
        return null;
    }


    public static void initServlets(Map<String, HttpServlet> map){
        try{
            InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("webapps/web.xml");
            SAXReader reader = new SAXReader();
            Document document = reader.read(stream);
            Element root = document.getRootElement();
            if(root==null){
                return;
            }
            List<Element> servlets = root.selectNodes("//servlet");
            for (Element servletParam : servlets) {
                ServletConfig config = new ServletConfig();
                config.setServletName(servletParam.elementText("servlet-name"));
                String className= servletParam.elementText("servlet-class");
                Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
                String pattern = servletParam.elementText("url-pattern");
                config.setUrlPattern(pattern);
                for (Object param : servletParam.elements("init-param")) {
                    String key = ((Element) param).elementText("param-name");
                    String value = ((Element) param).elementText("param-value");
                    config.getInitParams().put(key,value);
                }
                if(!HttpServlet.class.isAssignableFrom(clazz)){
                    throw new Exception();
                }
                HttpServlet servlet = (HttpServlet) clazz.getConstructor(ServletConfig.class).newInstance(config);
                if (map.containsKey(pattern)){
                    log.warn("MyTomcat: pattern "+pattern+"existed in more than 1 servlets, only the latter will be kept.");
                }
                map.put(pattern,servlet);
            }
        } catch (Exception e){
            log.warn("webapps/web.xml not found or illegal");
        }

    }

}
