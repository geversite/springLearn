package org.mysql.sql;

import lombok.Getter;
import org.mysql.table.Row;
import org.mysql.table.Types;

import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class SqlCondition {

    SqlCondition parent;
    String connection;
    String conditionL;
    String conditionR;
    @Getter
    String condition;
    SqlCondition childrenL;
    SqlCondition childrenR;


    /**
     * 返回conditionL为name的孩子
     * @param name
     * @return
     */
    public SqlCondition getColumn(String name) {
        if (conditionL != null && conditionL.equals(name)) {
            return this;
        }
        if (childrenL != null) {
            SqlCondition res = childrenL.getColumn(name);
            if (res != null) {
                return res;
            }
        }
        if (childrenR != null) {
            SqlCondition res = childrenR.getColumn(name);
            return res;
        }
        return null;
    }

    public int indexSearch(Object indexObj, Class indexType) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Object targetObj = Types.construct(conditionR.getBytes(StandardCharsets.UTF_8), indexType,0);
        return ((Comparable)targetObj).compareTo(indexObj);
    }


    public boolean match(Row row) throws Exception {
        if (!row.selectValid()){
            return false;
        }
        if (condition != null) {
            Object value;
            Class RType = row.getType(conditionL);
            if (RType != Date.class) {
                value = RType.getConstructor(String.class).newInstance(conditionR);
            }else {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd/HH:mm:ss");
                value = sdf.parse(conditionR);
            }
            if (condition.equals("==")) {
                return row.get(conditionL).equals(value);
            } else if (condition.equals("!=")) {
                return !row.get(conditionL).equals(value);
            } else if (condition.equals(">")) {
                return ((Comparable<Object>)row.get(conditionL)).compareTo(value) > 0;
            }else if (condition.equals("<")) {
                return ((Comparable<Object>)row.get(conditionL)).compareTo(value) < 0;
            }
            throw new Exception(condition + " is not a valid condition");
        } else {
            if (connection.equals("and")) {
                return childrenL.match(row)&&childrenR.match(row);
            }else if (connection.equals("or")) {
                return childrenL.match(row)||childrenR.match(row);
            }
        }
        throw new Exception(condition + " is not a valid condition");
    }

    public String getConditionKey() {
        return conditionL;
    }

}