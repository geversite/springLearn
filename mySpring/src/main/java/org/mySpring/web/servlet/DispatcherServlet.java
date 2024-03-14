package org.mySpring.web.servlet;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mySpring.context.BeanDefinition;
import org.mySpring.context.ApplicationContext;
import org.mySpring.annotation.Controller;
import org.mySpring.annotation.RequestBody;
import org.mySpring.annotation.ResponseBody;
import org.mySpring.lib.TypeSwitch;
import org.mySpring.web.annotation.RequestMapping;
import org.mySpring.web.annotation.RequestParam;
import org.mySpring.web.handler.MvcHandler;
import org.mySpring.web.xml.XmlParser;
import org.mylog.Logger;
import org.myTomcat.config.ServletConfig;
import org.myTomcat.entity.HttpRequest;
import org.myTomcat.entity.HttpResponse;
import org.myTomcat.entity.HttpServlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DispatcherServlet extends HttpServlet {

    private ApplicationContext applicationContext;
    private Map<String, MvcHandler> handlerMap = new HashMap<>();

    static Logger log = Logger.getLogger();

    public DispatcherServlet(ApplicationContext context){
        try {
            applicationContext = context;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public DispatcherServlet(ServletConfig config){
        this.servletConfig = config;
        String location = this.getServletConfig().getInitParameter("contextConfigLocation");
        try {
            String basePackage = XmlParser.getBasePackage(location.split(":")[1]);
            String[] basePackages = basePackage.split(",");
            applicationContext = new ApplicationContext(basePackages);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



    @Override
    public void init() {
        try {
            initHandlerMapping();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private void initHandlerMapping() throws Exception{
        if(applicationContext.getBeanDefinitionDict().isEmpty()){
            throw new Exception("no beans defined");
        }
        for (Map.Entry<String, BeanDefinition> entry : applicationContext.getBeanDefinitionDict().entrySet()) {
            String beanName = entry.getKey();
            BeanDefinition definition = entry.getValue();
            Class<?> clazz = definition.getClazz();
            String mapping = "";
            if(clazz.isAnnotationPresent(Controller.class)){
                if (clazz.isAnnotationPresent(RequestMapping.class)){
                    mapping = mapping + clazz.getAnnotation(RequestMapping.class).value();
                }
                Method[] methods = clazz.getDeclaredMethods();
                for (Method method : methods) {
                    if (method.isAnnotationPresent(RequestMapping.class)){
                        MvcHandler handler = new MvcHandler(applicationContext.getBean(beanName),method);
                        handlerMap.put(mapping + method.getAnnotation(RequestMapping.class).value(),handler);
                    }
                }
            }
        }
    }

    @Override
    public void doPost(HttpRequest req, HttpResponse resp){
        try {
            executeDispatch(req,resp);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void doGet(HttpRequest req, HttpResponse resp){
        try {
            executeDispatch(req,resp);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void executeDispatch(HttpRequest req, HttpResponse resp) throws IOException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        String URI = req.getRequestURI();
        MvcHandler handler = handlerMap.get(URI.split("\\?")[0]);
        if(handler==null){
            try (InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(req.getRequestURI().substring(1))) {
                if (stream == null) {
                    resp.setStatus(404, "Not Found");
                    resp.addHeader("Content-Type", "text/html; charset=UTF-8");
                    return;
                }
                StringBuilder builder = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                resp.writeBody(builder.toString());
                return;
            }
        }
        Method method = handler.getMethod();
        Parameter[] handlerParameters = method.getParameters();
        Class<?>[] handlerParameterTypes = method.getParameterTypes();
        Annotation[][] handlerParameterAnnotations = method.getParameterAnnotations();
        Map<String, String[]> requestParameterMap = req.getParameterMap();
        Object[] params = new Object[handlerParameterTypes.length];

        for (int i = 0; i < handlerParameterTypes.length; i++) {
            Class<?> type = handlerParameterTypes[i];
            if (type.isInstance(req)) {
                params[i] = req;
            }
            if (type.isInstance(resp)) {
                params[i] = resp;
            }
            for (Annotation annotation : handlerParameterAnnotations[i]) {
                if (annotation instanceof RequestParam && !((RequestParam) annotation).value().equals("")) {
                    String key = ((RequestParam) annotation).value();
                    if (!requestParameterMap.containsKey(key)) {
                        resp.setStatus(400,"Bad Request");
                        return;
                    }
                    String value = requestParameterMap.get(((RequestParam) annotation).value())[0];
                    params[i] = handlerParameterTypes[i].getConstructor(String.class).newInstance(value);
                }
                if (annotation instanceof RequestBody) {
                    String requestBody = req.getBody();
                    if (handlerParameterTypes[i].isInstance(String.class)) {
                        params[i] = requestBody;
                    } else {
                        params[i] = parseString(requestBody, handlerParameterTypes[i]);
                    }
                }
            }
            String paramName = handlerParameters[i].getName();
            if (paramName.startsWith("arg")) {
                log.warn("You may have neither preserved parameter names at compile time nor used the @RequestParam annotation correctly.");
            }
            if (requestParameterMap.containsKey(paramName)) {
                String value = requestParameterMap.get(paramName)[0];
                if(handlerParameterTypes[i].isPrimitive()){
                    params[i] = TypeSwitch.doSwitch(handlerParameterTypes[i]).getConstructor(String.class).newInstance(value);
                }else {
                    params[i] = handlerParameterTypes[i].getConstructor(String.class).newInstance(value);
                }
            }
        }



        Object ret = method.invoke(handler.getController(),params);

        if (method.isAnnotationPresent(ResponseBody.class)){
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(ret);
            if(method.getReturnType()==String.class)
                json = (String)ret;
            resp.writeBody(json);
        } else if (ret instanceof String){
            String view = (String) ret;
            if (view.contains(":")){
                String action = view.split(":")[0];
                String name = view.split(":")[1];
                if(action.equals("redirect")){
                    resp.sendRedirect(name);
                }else {
//                    req.getRequestDispatcher(name).forward(req,resp);
                    resp.sendRedirect(view);

                }
            }else {
//                req.getRequestDispatcher(view).forward(req,resp);
                resp.sendRedirect(view);

            }
        }


    }

    private Object parseString(String requestBody,Class<?> clazz) {
        ObjectMapper mapper = new ObjectMapper();
        try{
            if(clazz.isInstance(Map.class)){
                return mapper.<Map<String, String>>readValue(requestBody, new TypeReference<Map<String, String>>() {});
            } else if (clazz.isInstance(List.class)){
                return mapper.<List<String>>readValue(requestBody, new TypeReference<List<String>>() {});
            } else {
                return mapper.readValue(requestBody, clazz);
            }
        }catch (Exception e){
            return null;
        }


    }
}
