package org.mysql.sql;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    String sql;
    Pattern select;
    Pattern insert;
    Pattern update;
    Pattern delete;

    public Parser(String sql) {
        this.sql = sql;
        String regexR = "select\\s+(.+)\\s+from\\s+(.+)\\s+where\\s+(.+);";
        this.select = Pattern.compile(regexR);
        String regexC = "insert into\\s+(.+)\\s+values\\s+(.+);";
        this.insert = Pattern.compile(regexC);
        String regexU = "update\\s+(.+)\\s+set\\s+(.+)\\s+where\\s+(.+);";
        this.update = Pattern.compile(regexU);
        String regexD = "delete from\\s+(.+)\\s+where\\s+(.+);";
        this.delete = Pattern.compile(regexD);

    }

    public SqlMeta parse() {
        SqlMeta meta = new SqlMeta();
        meta.sql = sql.toLowerCase();
        Matcher MSelect = select.matcher(meta.sql);
        Matcher MInsert = insert.matcher(meta.sql);
        Matcher MUpdate = update.matcher(meta.sql);
        Matcher MDelete = delete.matcher(meta.sql);
        if (MInsert.matches()) {
            meta.type = "INSERT";
            meta.table = MInsert.group(1);
            String values = MInsert.group(2);
            values = values.trim().substring(1, values.length() - 1);
            meta.values = Arrays.asList(values.split(","));
        } else if (MUpdate.matches()) {
            meta.type = "UPDATE";
            meta.table = MUpdate.group(1);
            String scope = MUpdate.group(2);
            if (scope.equals("*")) {
                meta.scopeAll = true;
            }else {
                scope = scope.trim().substring(1, scope.length() - 1);
                meta.values = Arrays.asList(scope.split(","));
            }
            String condition = MUpdate.group(3).trim();
            meta.condition = parseCondition(condition);
        }else if (MSelect.matches()) {
            meta.type = "SELECT";
            meta.table = MSelect.group(2);
            String scope = MSelect.group(2);
            if (scope.equals("*")) {
                meta.scopeAll = true;
            }else {
                scope = scope.trim().substring(1, scope.length() - 1);
                meta.values = Arrays.asList(scope.split(","));
            }
            String condition = MSelect.group(3).trim();
            meta.condition = parseCondition(condition);
        }
        return meta;
    }


    private SqlCondition parseCondition(String condition) {
        SqlCondition root = new SqlCondition();
        if (condition.startsWith("(") && condition.endsWith(")")) { //嵌套
            List<String> elements = extractBrackets(condition);
            SqlCondition conditionL = parseCondition(elements.get(0).trim());
            SqlCondition conditionR = parseCondition(elements.get(1).trim());
            root.childrenL = conditionL;
            root.childrenR = conditionR;
            root.connection = elements.get(2).trim();
        } else { //简单
            List<String> elements = Arrays.asList(condition.split(" "));
            root.condition = elements.get(1).trim();
            root.conditionL = elements.get(0).trim();
            root.conditionR = elements.get(2).trim();
        }
        return root;
    }


    private List<String> extractBrackets(String input) {
        // 初始化返回结果
        List<String> result = new ArrayList<>();
        result.add(""); // 左括号内容
        result.add(""); // 括号间内容
        result.add(""); // 右括号内容

        // 使用栈来跟踪括号的位置
        Stack<Integer> stack = new Stack<>();

        // 记录最外层括号的起始和结束位置
        int outerStart = -1;
        int outerEnd = -1;

        // 遍历字符串
        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);
            if (ch == '(') {
                stack.push(i); // 将 '(' 的位置压入栈
                if (stack.size() == 1) {
                    outerStart = i; // 记录最外层括号的起始位置
                }
            } else if (ch == ')') {
                if (!stack.isEmpty()) {
                    int start = stack.pop(); // 弹出栈顶的 '(' 位置
                    if (stack.isEmpty()) {
                        outerEnd = i; // 记录最外层括号的结束位置
                    }
                }
            }
        }

        // 检查是否找到最外层括号
        if (outerStart != -1 && outerEnd != -1) {
            // 提取左括号内容
            result.set(0, input.substring(outerStart + 1, outerEnd));

            // 提取括号间内容（yyy 部分）
            int yyyStart = outerEnd + 1;
            int yyyEnd = input.indexOf('(', yyyStart);
            if (yyyEnd == -1) {
                yyyEnd = input.length();
            }
            result.set(1, input.substring(yyyStart, yyyEnd));

            // 提取右括号内容
            if (yyyEnd < input.length() && input.charAt(yyyEnd) == '(') {
                int rightEnd = input.indexOf(')', yyyEnd);
                if (rightEnd != -1) {
                    result.set(2, input.substring(yyyEnd + 1, rightEnd));
                }
            }
        }

        return result;
    }

}
