package com.zxb.test;

import org.myTomcat.core.MyTomcat;


public class tomcatTest {
    public static void main(String[] args) throws Exception {
        MyTomcat tomcat = new MyTomcat();
        tomcat.start();
    }
}
