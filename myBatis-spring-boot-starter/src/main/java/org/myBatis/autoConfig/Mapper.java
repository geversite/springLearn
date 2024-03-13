package org.myBatis.autoConfig;

import org.mySpring.annotation.AliasFor;
import org.mySpring.annotation.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface Mapper {

    @AliasFor(Component.class)
    String value() default "";

}
