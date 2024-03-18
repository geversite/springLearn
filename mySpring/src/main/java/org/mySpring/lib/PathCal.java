package org.mySpring.lib;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;

public class PathCal {
    public static String getClassPathRelativePath(File file, ClassLoader classLoader) throws URISyntaxException, IOException {
        String filePath = file.getCanonicalPath();

        URL resourceUrl = classLoader.getResource("");
        if (resourceUrl != null) {
            String classPathRoot = new File(resourceUrl.toURI()).getCanonicalPath();

            if (filePath.startsWith(classPathRoot)) {
                return filePath.substring(classPathRoot.length() + 1);
            }
        }

        throw new RuntimeException("The file "+filePath+" is not on the classpath.");
    }
}
