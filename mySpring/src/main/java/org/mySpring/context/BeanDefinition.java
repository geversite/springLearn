package org.mySpring.context;

public class BeanDefinition {

    Class<?> clazz;
    String scope;

    BeanBuilder builder;

    public BeanBuilder getBuilder() {
        return builder;
    }

    public void setBuilder(BeanBuilder builder) {
        this.builder = builder;
    }


    public BeanDefinition(Class<?> clazz, String scope) {
        this.clazz = clazz;
        this.scope = scope;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }
}
