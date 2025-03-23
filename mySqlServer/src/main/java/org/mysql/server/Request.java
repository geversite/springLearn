package org.mysql.server;

import lombok.Data;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

@Data
public class Request {

    private String data;
    private RequestType requestType;
    private String requestContent;
    private String[] paramTypes;
    private String[] paramValues;

    public Request(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        data = reader.readLine();
        parse();
    }

    private void parse() {

        List<String> parts = extractNestedBracketedStrings(this.data);

        RequestType requestType  = RequestType.valueOf(parts.get(0));
        String requestContent = parts.get(1);
        this.requestType = requestType;
        this.requestContent = requestContent;
        List<String> params = extractNestedBracketedStrings(parts.get(3));
        this.paramValues = new String[params.size()];
        this.paramTypes = new String[params.size()];
        for (int i = 0; i < params.size(); i++) {
            this.paramTypes[i] = params.get(i).split(":")[0];
            this.paramValues[i] = params.get(i).split(":")[1];
        }
    }

    public static List<String> extractNestedBracketedStrings(String input) {
        List<String> results = new ArrayList<>(); // 存储所有匹配的内容
        Stack<Integer> stack = new Stack<>(); // 用于跟踪嵌套的方括号
        StringBuilder currentString = new StringBuilder(); // 当前正在处理的字符串

        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);

            if (ch == '[') {
                stack.push(i); // 遇到 '[' 入栈
                if (stack.size() == 1) {
                    currentString = new StringBuilder(); // 开始新的匹配
                } else {
                    currentString.append(ch); // 嵌套部分继续记录
                }
            } else if (ch == ']') {
                if (!stack.isEmpty()) {
                    stack.pop(); // 遇到 ']' 出栈
                    if (stack.isEmpty()) { // 栈为空时，表示匹配到一个完整的内容
                        results.add(currentString.toString());
                    } else {
                        currentString.append(ch); // 嵌套部分继续记录
                    }
                } else {
                    throw new IllegalArgumentException("Invalid input: unmatched ']'");
                }
            } else {
                if (!stack.isEmpty()) {
                    currentString.append(ch); // 记录方括号内的内容
                }
            }
        }

        if (!stack.isEmpty()) {
            throw new IllegalArgumentException("Invalid input: unmatched '['");
        }

        return results;
    }
}
