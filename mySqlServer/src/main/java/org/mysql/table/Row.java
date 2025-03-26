package org.mysql.table;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Row {

    private List<String> columnNames;
    private boolean valid;
    private boolean delete;
    private  Map<String, Class> typeMap;
    private Map<String, Object> objectMap = new HashMap<String, Object>();

    public Row(byte[] byteRow, List<String> columnNames, List<Class> columnTypes) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        this.columnNames = columnNames;
        byte[] head = Arrays.copyOfRange(byteRow, 0, Table.HEADSIZE);
        valid = ((head[0] & RecordHead.valid)!=0);
        delete = ((head[1] & RecordHead.delete)!=0);
        if (valid && !delete) {
            int index = Table.HEADSIZE;
            for (int i = 0; i < columnNames.size(); i++) {
                Object value = Types.construct(byteRow, columnTypes.get(i), index);
                index+=Types.getLength(columnTypes.get(i));
                objectMap.put(columnNames.get(i), value);
                typeMap.put(columnNames.get(i), columnTypes.get(i));
            }
        }
    }

    public Object get(String columnName) {
        return objectMap.get(columnName);
    }

    public Class getType(String columnName) {
        return typeMap.get(columnName);
    }

    public boolean selectValid(){
        return valid && !delete;
    }
}
