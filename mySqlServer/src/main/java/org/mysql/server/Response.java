package org.mysql.server;

import lombok.Data;

import java.util.List;

@Data
public class Response {
    private SqlType sqlType;
    private RespType status;
    private String message;
    private int modify;
    private List<String> columnNames;
    private List<String> columnTypes;
    private List<List<String>> rows;

    public Response() {

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
