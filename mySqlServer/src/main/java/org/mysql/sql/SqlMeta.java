package org.mysql.sql;

import lombok.Data;

import java.util.List;

@Data
public class SqlMeta {

    String sql;
    String type;
    boolean scopeAll;
    List<String> scope;
    String table;
    SqlCondition condition;
    List<String> values;

}
