package org.mySpring.context;

import org.mySpring.annotation.AliasFor;
import org.mySpring.annotation.Import;

import java.lang.annotation.Annotation;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.*;

public class ClassMetaData {
    Class<?> clazz;
    Map<Class<?>, Annotation> anno = new HashMap<>();

    Map<Class<?>, Object> annoValue = new HashMap<>();

    public ClassMetaData(Class<?> clazz) throws Exception {
        this.clazz = clazz;
        annoValue.put(Import.class, new ArrayList<Class<?>>());
        for (Annotation annotation : clazz.getDeclaredAnnotations()) {
            addAnno(annotation);
        }
    }

    public <T> T getAnnotation(Class<T> clazz){
        return (T) anno.get(clazz);
    }

    public Object getAnnotationValue(Class<?> clazz){
        return annoValue.get(clazz);
    }

    public boolean isAnnotationPresent(Class<?> clazz){
        return anno.containsKey(clazz);
    }

    public boolean isAnnotationValuePresent(Class<?> clazz){
        return annoValue.containsKey(clazz);
    }

    private void addAnno(Annotation annotation) throws Exception {
        if(annotation.annotationType() == Import.class){
            anno.put(Import.class, annotation);
            ((List<Class<?>>)annoValue.get(Import.class)).add(((Import) annotation).value());
            return;
        }
        anno.put(annotation.annotationType(),annotation);

        for (Annotation annotation1 : annotation.annotationType().getDeclaredAnnotations()) {
            if(!anno.containsKey(annotation1.annotationType()) || annotation1.annotationType() == Import.class)
                addAnno(annotation1);
        }
        for (Method method: annotation.annotationType().getDeclaredMethods()){
            if(method.isAnnotationPresent(AliasFor.class)){
                Class<?> alias = method.getDeclaredAnnotation(AliasFor.class).value();
                annoValue.put(alias,method.invoke(annotation));
            }else {
                annoValue.put(annotation.annotationType(),method.invoke(annotation));
            }
        }
    }
}