package org.mySpring.lib;

public class BeanLib {

    public static String getBeanName(Class<?> c){
        return c.getName().substring(0,1).toLowerCase()+c.getName().substring(1);
    }
}
