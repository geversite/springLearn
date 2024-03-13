package org.mySpring.lib;

import java.util.HashMap;
import java.util.Map;

public class TypeSwitch {

    private static final Map<Class<?>, Class<?>> map;

    private static final Map<String, Class<?>> str2class;

    static {
        map = new HashMap<>();
        map.put(int.class, Integer.class);
        map.put(char.class, Character.class);
        map.put(boolean.class, Boolean.class);
        map.put(float.class, Float.class);
        map.put(double.class, Double.class);
        map.put(short.class, Short.class);
        map.put(long.class, Long.class);
        str2class = new HashMap<>();
        str2class.put("int", int.class);
        str2class.put("char", char.class);
        str2class.put("boolean", boolean.class);
        str2class.put("float", float.class);
        str2class.put("double", double.class);
        str2class.put("short", short.class);
        str2class.put("long", long.class);
    }

    public static Class<?> doSwitch(Class<?> c){
        return map.get(c)==null?c:map.get(c);
    }

    public static Class<?> loadClass(String s){
        try {
            return str2class.containsKey(s)?str2class.get(s):Thread.currentThread().getContextClassLoader().loadClass(s);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }



}
