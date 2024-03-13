package org.myBatis.utils;

import org.myBatis.configuration.BoundSql;
import org.myBatis.executor.Param;

import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SqlParser {

    public static BoundSql parse(String originSql, Parameter[] parameters, Object... params) throws Exception {
        BoundSql sql = parseToken(originSql);
        parseParams(sql,parameters, params);
        return sql;
    }

    private static void parseParams(BoundSql sql, Parameter[] parameters, Object[] params) throws Exception {
        Map<String, Object> map = new HashMap<>();
        Object defaultObj = null;
        if (parameters.length==0){
            return;
        } else if (parameters.length == 1) {
            defaultObj = params[0];
            if (parameters[0].isAnnotationPresent(Param.class)){
                String key = parameters[0].getAnnotation(Param.class).value();
                map.put(key.equals("")?parameters[0].getName():key,params[0]);
            }else {
                map.put(parameters[0].getName(),params[0]);
            }
        } else {
            for (int i = 0; i<parameters.length;i++) {
                if (parameters[i].isAnnotationPresent(Param.class)){
                    String key = parameters[i].getAnnotation(Param.class).value();
                    map.put(key.equals("")?parameters[i].getName():key,params[i]);
                }else {
                    map.put(parameters[i].getName(),params[i]);
                }
            }
        }
        for(String paramName : sql.getParamNames()){
            String[] keys = paramName.split("\\.");
            int i=0;
            Object obj = map.get(keys[i++]);
            if (obj == null){
                obj = defaultObj;
                i--;
            }
            if(obj == null){
                throw new Exception("ParamNotFound: "+keys);
            }
            for(;i< keys.length;i++){
                Field field = obj.getClass().getDeclaredField(keys[i]);
                field.setAccessible(true);
                obj = field.get(obj);
//                String methodName = "get" + keys[i].substring(0,1).toUpperCase() + keys[i].substring(1);
//                Method method = obj.getClass().getMethod("methodName");
//                Object result = method.invoke(obj);
//                obj = result;
            }
            sql.getParams().add(obj);
        }
    }

    private static BoundSql parseToken(String originSql) throws Exception {
        BoundSql sql = new BoundSql();
        List<Integer> left = new ArrayList<>();
        List<Integer> right = new ArrayList<>();
        int cnt = 0;
        char[] sqlChars = originSql.toCharArray();
        for(int i=0;i<sqlChars.length;i++){
            if(sqlChars[i]=='{'){
                left.add(i);
            } else if (sqlChars[i] == '}') {
                right.add(i);
            } else if (sqlChars[i] == '#') {
                cnt++;
            }
        }
        if ((left.size()!= right.size()) || (left.size()!=cnt)){
            throw new Exception("Illegal mybatis sql string!");
        }
        for(int i=0; i<left.size();i++){
            sql.getParamNames().add(originSql.substring(left.get(i)+1,right.get(i)));
        }
        sql.setSql(originSql.replaceAll("#\\{(.*?)\\}","?"));
        return sql;
    }
}
