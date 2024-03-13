package org.mySpring.cloud.feign;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

public class DeSerializer {
    public static Object[] deserializeParams(String jsonParams, String[] paramTypes, ObjectMapper objectMapper) throws IOException, ClassNotFoundException {
        // 将 JSON 参数字符串反序列化为 List<Object>
        List<Object> tempParamList = objectMapper.readValue(jsonParams, List.class);

        Object[] deserializedParams = new Object[paramTypes.length];
        for (int i = 0; i < paramTypes.length; i++) {
            String typeName = paramTypes[i];
            Object paramValue = tempParamList.get(i);

            // 处理基本数据类型和 void 类型
            if (typeName.equals("int") || typeName.equals("java.lang.Integer")) {
                deserializedParams[i] = ((Number) paramValue).intValue();
            } else if (typeName.equals("long") || typeName.equals("java.lang.Long")) {
                deserializedParams[i] = ((Number) paramValue).longValue();
            } else if (typeName.equals("double") || typeName.equals("java.lang.Double")) {
                deserializedParams[i] = ((Number) paramValue).doubleValue();
            } else if (typeName.equals("float") || typeName.equals("java.lang.Float")) {
                deserializedParams[i] = ((Number) paramValue).floatValue();
            } else if (typeName.equals("boolean") || typeName.equals("java.lang.Boolean")) {
                deserializedParams[i] = Boolean.parseBoolean(paramValue.toString());
            } else if (typeName.equals("char") || typeName.equals("java.lang.Character")) {
                deserializedParams[i] = paramValue.toString().charAt(0);
            } else if (typeName.equals("byte") || typeName.equals("java.lang.Byte")) {
                deserializedParams[i] = Byte.parseByte(paramValue.toString());
            } else if (typeName.equals("short") || typeName.equals("java.lang.Short")) {
                deserializedParams[i] = Short.parseShort(paramValue.toString());
            } else if (typeName.equals("void")) {
                deserializedParams[i] = null;
            } else {
                // 处理引用类型
                Class<?> paramClass = Thread.currentThread().getContextClassLoader().loadClass(typeName);
                JavaType paramJavaType = objectMapper.constructType(paramClass);
                deserializedParams[i] = objectMapper.convertValue(paramValue, paramJavaType);
            }
        }
        return deserializedParams;
    }
}
