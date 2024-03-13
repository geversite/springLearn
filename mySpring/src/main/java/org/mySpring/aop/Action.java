package org.mySpring.aop;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.sf.cglib.proxy.MethodProxy;

@AllArgsConstructor
@Data
public class Action {
    MethodProxy method;

    Object object;

    Object[] params;

    public Object run() throws Throwable {
        return method.invokeSuper(object, params);
    }

}
