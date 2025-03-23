package org.mysql.sql;

import java.util.Arrays;
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

        }




        return meta;
    }


    private SqlCondition parseCondition(String condition) {
        SqlCondition root = new SqlCondition();
        return root;
    }

}
