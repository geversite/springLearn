package org.mySpring.boot;

import org.mySpring.context.ClassMetaData;

import java.lang.annotation.Annotation;

public class AnnotationUtils {

    public static Annotation getAnnotation(Class<?> clazz, Class<?> anno) throws Exception {
        ClassMetaData metaData = new ClassMetaData(clazz);
        Annotation annotation = (Annotation) metaData.getAnnotation(anno);
        return annotation;
    }
}
