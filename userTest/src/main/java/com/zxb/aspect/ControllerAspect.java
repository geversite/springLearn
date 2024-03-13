package com.zxb.aspect;

import org.mySpring.annotation.Component;
import org.mySpring.aop.Action;
import org.mySpring.aop.Around;
import org.mySpring.aop.Aspect;
import org.mylog.Logger;

@Aspect
@Component
public class ControllerAspect {

    public static Logger log = Logger.getLogger();

    @Around("com.zxb.controller.*.*")
    public Object log(Action action) throws Throwable {
        log.info(action.getObject().getClass().getSimpleName()+"."+action.getMethod().getSuperName()+" is invoked!");
        return action.run();
    }

}
