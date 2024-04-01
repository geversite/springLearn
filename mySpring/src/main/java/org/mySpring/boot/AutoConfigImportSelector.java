package org.mySpring.boot;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;


public class AutoConfigImportSelector  {

    public String[] selectImports() throws IOException {
        Set<URL> urls = getAllDependencyJars();
        List<String> beans = new ArrayList<>();
        for (URL jarUrl : urls) {
            URL fileUrl = new URL("jar:" + jarUrl + "!/META-INF/spring.factories");
            JarURLConnection jarConnection = (JarURLConnection) fileUrl.openConnection();
            try(JarFile jarFile = jarConnection.getJarFile()) {
                JarEntry jarEntry = jarConnection.getJarEntry();
                if (jarEntry != null && !jarEntry.isDirectory()) {
                    try (InputStream inputStream = jarFile.getInputStream(jarEntry)) {
                        List<String> bs = getBeans(inputStream);
                        beans.addAll(bs);
                    }
                }
            }catch (Exception ignored){}
        }

        return beans.toArray(new String[0]);
    }

    private List<String> getBeans(InputStream stream) throws IOException {
        Properties properties = new Properties();
        properties.load(stream);
        String configs = (String) properties.get("autoConfigure");
        String[] split = configs.split(",");
        return Arrays.asList(split);
    }


    public static Set<URL> getAllDependencyJars() {
        Set<URL> urls = new HashSet<>();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        if (classLoader instanceof URLClassLoader) {
            URLClassLoader urlClassLoader = (URLClassLoader) classLoader;
            for (URL url : urlClassLoader.getURLs()) {
                if (url.getFile().endsWith(".jar")) {
                    urls.add(url);
                }else {
                    try {
                        urls.add(findFirstJarUrl(url));
                    }catch (Exception e){
                    }
                }
            }
        } else {
            try {
                Enumeration<URL> enumeration = classLoader.getResources("META-INF/MANIFEST.MF");
                while (enumeration.hasMoreElements()) {
                    URL url = enumeration.nextElement();
                    // Filter out JAR files based on the URL
                    if (url.getProtocol().equals("jar")) {
                        String jarUrl = url.getFile();
                        int separatorIndex = jarUrl.indexOf("!");
                        if (separatorIndex != -1) {
                            jarUrl = jarUrl.substring(5, separatorIndex); // Remove "jar:" prefix and "!/META-INF/MANIFEST.MF" suffix
                            urls.add(new URL(jarUrl));
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return urls;
    }

    public static URL findFirstJarUrl(URL url) throws Exception {
        // 获取原始 URL 的父目录
        URL parentUrl = new URL(url, "..");
        Path parentPath = Paths.get(parentUrl.getPath().substring(1));

        // 使用 Files.list 流式处理目录下的文件
        Stream<Path> list = Files.list(parentPath);
        Stream<Path> pathStream = list.filter(path -> path.toString().endsWith(".jar"));
        Optional<Path> first = pathStream.findFirst();
        Path path = first.get();
        URI uri = path.toUri();
        URL url1 = uri.toURL();
        return url1;
    }
}
