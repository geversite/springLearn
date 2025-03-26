package org.mysql.server;

import lombok.Data;
import org.mySql.client.Exception.SqlException;
import org.mySql.client.connection.ResultSet;
import org.mysql.table.Table;
import org.mysql.table.Types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class Response {
    private SqlType sqlType;
    private RespType status;
    private String message;
    private int modify;
    private List<String> columnNames;
    private List<String> columnTypes;
    private List<List<String>> rows;

    public Response(){}

    public Response(ResultSet set) throws SqlException {
        this.sqlType = SqlType.QUERY;
        this.status = RespType.OK;
        this.message="";
        this.modify = 0;
        this.columnNames = new ArrayList<>();
        Map<String, Integer> columnMap = set.getColumn_map();
        for (String columnName : columnMap.keySet()) {
            int columnNum = columnMap.get(columnName);
            this.columnNames.set(columnNum, columnName);
        }
        this.columnTypes = new ArrayList<>();
        for (Class type: set.getMetadata()) {
            columnTypes.add(type.getSimpleName().toUpperCase());
        }
        this.rows = new ArrayList<>();
        for (List<Object> row: set.getAlldata()){
            List<String> rowsStr = new ArrayList<>(rows.size());
            for (Object obj: row){
                rowsStr.add(Types.toStr(obj));
            }
            this.rows.add(rowsStr);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[RESULT][").append(status).append("]");
        if (sqlType == SqlType.QUERY) {
            sb.append("[COLUMNS]");
            for (int i = 0; i < columnNames.size(); i++) {
                sb.append("[").append(columnTypes.get(i)).append(":").append(columnNames.get(i)).append("]");
            }
            sb.append("[ROWS][");
            for (int i = 0; i < rows.size(); i++) {
                sb.append("[");
                for (int j = 0; j < rows.get(i).size(); j++) {
                    sb.append(rows.get(i).get(j));
                    if (j < rows.get(i).size() - 1) {
                        sb.append(",");
                    }
                }
                sb.append("]");
            }
            sb.append("]");
        }else if (sqlType == SqlType.UPDATE) {
            sb.append("[MODIFY]");
            sb.append("[").append(modify).append("]");
        }
        sb.append("\n");
        return sb.toString();
    }
}
