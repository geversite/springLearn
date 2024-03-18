package org.myHttp.lib;

import org.mylog.Logger;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class URIParser {

    public static Logger log = Logger.getLogger();

    public static Map<String, String[]> parseURI(String uriStr) throws URISyntaxException {
        Map<String, List<String>> parameterMap = new HashMap<>();
        Map<String, String[]> stringMap = new HashMap<>();
        URI uri = null;
        uri = new URI(uriStr);

        String query = uri.getQuery(); // 获取查询部分

        if (query != null) {
            String[] pairs = query.split("&"); // 分割每个参数对
            for (String pair : pairs) {
                int idx = pair.indexOf("="); // 查找键值分隔符
                String key = idx > 0 ? pair.substring(0, idx) : pair; // 获取键
                String value = idx > 0 && pair.length() > idx + 1 ? pair.substring(idx + 1) : null; // 获取值
                if(!parameterMap.containsKey(key)){
                    parameterMap.put(key,new ArrayList<>());
                }
                parameterMap.get(key).add(value);
            }
        }
        for (Map.Entry<String, List<String>> entry : parameterMap.entrySet()) {
            stringMap.put(entry.getKey(),entry.getValue().toArray(new String[0]));
        }
        return stringMap;
    }
}
