package org.mySpring.context;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BeanBuilder {

    String beanName;
    Method method;
    Object[] params;
}
