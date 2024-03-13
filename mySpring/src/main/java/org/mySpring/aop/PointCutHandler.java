package org.mySpring.aop;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.lang.reflect.Method;

@AllArgsConstructor
@Data
public class PointCutHandler {

    String callerName;
    Method method;
}
