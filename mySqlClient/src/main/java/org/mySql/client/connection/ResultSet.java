package org.mySql.client.connection;

import lombok.Getter;
import org.mySql.client.Exception.SqlException;

import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResultSet {


    private Object[] thisRow;
    @Getter
    private  List<Class> metadata=new ArrayList<>();
    @Getter
    private Map<String,Integer> column_map=new HashMap<>();
    private int column_count=0;
    @Getter
    private List<List<Object>> alldata;
    private int row_count=0;

    public ResultSet(List<List<Object>> alldata, List<Class> metadata, List<String> columns) throws SqlException {
        this.alldata = alldata;
        this.metadata = metadata;
        for (int i = 0; i < columns.size(); i++) {
             column_map.put(columns.get(i), i);
        }

    }

    public ResultSet(String data) throws SqlException {
        String regex = "\\[(RESULT|COLUMNS|ROWS)\\](.*?)(?=\\[RESULT|\\[COLUMNS|\\[ROWS|$)";
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(data);

        Map<String, String> extractedInfo = new HashMap<>();

        List<String> dataList=extractNestedBracketedStrings(data);
        for(int i=0;i<dataList.size();i++){
            extractedInfo.put(dataList.get(i), dataList.get(i+1));
            i++;
        }
        if(extractedInfo.get("RESULT").equals("ERR")){
            throw new SqlException(extractedInfo.get("MSG"));
        }

        Map<String, String> columns=new HashMap<>();
        List<String> columnList=extractNestedBracketedStrings(extractedInfo.get("COLUMNS"));
        columns=parseColumns(columnList);

        for(String column:columns.keySet()){
            Class<?> type = getTypeClass(column);
            metadata.add(type);

            column_map.put(columns.get(column), column_count);
            column_count+=1;
        }

        List<String> rowList=extractNestedBracketedStrings(extractedInfo.get("ROWS"));
        alldata=parseRows(rowList);


    }

    @Override
    public String toString() {
        return  "!23113";
    }

    public boolean next() throws SqlException {
        if(row_count==alldata.size()){
            return false;
        }
        thisRow=new Object[metadata.size()];
        int i=0;
        for(Object o:alldata.get(row_count)){
            thisRow[i]=o;
            i+=1;
        }
        row_count+=1;
        return true;
    }

    public Object getObject(int index) throws SqlException {
        return thisRow[index];
    }

    public Object getObject(String column) throws SqlException {
        int index=column_map.get(column);
        return thisRow[index];
    }


    private Map<String, String> parseColumns(List<String> columnPairs) {
        Map<String, String> columns=new HashMap<>();
        for (String pair : columnPairs) {
            String[] keyValue = pair.split(": ");
            if (keyValue.length == 2) {
                columns.put(keyValue[0].trim(), keyValue[1].trim());
            }
        }
        return columns;
    }

    private List<List<Object>> parseRows(List<String> rowEntries) {
        List<List<Object>> rows = new ArrayList<>();
        for (String entry : rowEntries) {
            List<Object> row = new ArrayList<>();
            String[] values = entry.split(",");
            for (String value : values) {
                row.add(value.trim());
            }
            rows.add(row);
        }
        return rows;
    }

    private Class<?> getTypeClass(String typeStr) {
        switch (typeStr.toUpperCase()) {
            case "STRING":
                return String.class;
            case "FLOAT":
                return Float.class;
            case "DATE":
                return Date.class;
            case "SHORT":
                return Short.class;
            case "BOOLEAN":
                return Boolean.class;
            case "LONG":
                return Long.class;
            case "DOUBLE":
                return Double.class;
            case "INTEGER":
                return Integer.class;
            case "CHARACTER":
                return Character.class;
            default:
                throw new IllegalArgumentException("Unknown type: " + typeStr);
        }
    }

    private List<String> extractNestedBracketedStrings(String input) {
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
