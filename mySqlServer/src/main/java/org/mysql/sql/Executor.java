package org.mysql.sql;

import org.mySql.client.Exception.SqlException;
import org.mySql.client.connection.ResultSet;
import org.mysql.server.ConnectionInfo;
import org.mysql.server.Response;
import org.mysql.server.SqlType;
import org.mysql.table.DataBase;
import org.mysql.table.Row;
import org.mysql.table.Table;
import org.mysql.table.Types;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Executor {

    ConnectionInfo info;
    SqlMeta meta;


    public Executor(SqlMeta meta, ConnectionInfo info) {
        this.meta = meta;
        this.info = info;
    }

    public Response execute() throws Exception {
        if (Objects.equals(meta.type, "SELECT")) {
            return executeQuery();
        } else {
            return executeUpdate();
        }

    }

    private Response executeUpdate() {
        return null;
    }


    private Response executeQuery() throws Exception {
        DataBase dataBase = DataBase.DataBases.get(info.getDb());
        Table table = dataBase.getTable(meta.getTable());
        List<Row> rows = table.getRows(meta.getCondition());
        //rows未经过滤
        ResultSet set;
        if (meta.isScopeAll()){
            set = getResultSet(rows, table.getColumnNames(), table);
        }else {
            set = getResultSet(rows, meta.getScope(), table);

        }
        //set经过过滤
        return new Response(set);
    }

    private ResultSet getResultSet(List<Row> rows, List<String> scope, Table table) throws SqlException {
        List<Class> classes = new ArrayList<>();
        for (String s : scope) {
            classes.add(table.getColumnTypes().get(table.getColumnNames().indexOf(s)));
        }
        List<List<Object>> data = new ArrayList<>();
        for (Row row : rows) {
            List<Object> rowData = new ArrayList<>();
            for (String s : scope) {
                rowData.add(row.get(s));
            }
            data.add(rowData);
        }
        ResultSet resultSet = new ResultSet(data, classes, scope);
        return resultSet;
    }
}
