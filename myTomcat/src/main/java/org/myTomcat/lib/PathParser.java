package org.myTomcat.lib;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PathParser {

    public static boolean match(String uri, String pattern){
        String regex = convertWildcardToRegex(pattern);
        // 创建 Pattern 对象
        Pattern p = Pattern.compile(regex);

        // 创建 matcher 对象
        Matcher m = p.matcher(uri);

        // 判断 URI 是否符合模式
        return m.matches();
    }

    private static String convertWildcardToRegex(String pattern) {
        String regex = "^" + pattern
                .replace(".", "\\.")
                .replace("*", ".*") // 将 * 转换为 .*（匹配任意字符）
                .replace("?", ".")  // 将 ? 转换为 .（匹配单个字符）
                + "$";
        return regex;
    }
}
