package org.myBatis.io;

import java.io.InputStream;

public class Resources {

    public static InputStream getResourceAsStream(String path){
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
    }
}
