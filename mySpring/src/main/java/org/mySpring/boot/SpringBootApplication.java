package org.mySpring.boot;

import org.mySpring.annotation.ComponentScan;
import org.mySpring.annotation.Configuration;
import org.mySpring.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@ComponentScan
@AutoConfiguration
@Configuration
public @interface SpringBootApplication {

}
