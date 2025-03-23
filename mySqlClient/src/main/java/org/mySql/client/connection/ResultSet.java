package org.mySql.client.connection;

import lombok.Getter;
import org.mySql.client.Exception.SqlException;

import java.io.InputStream;
import java.util.List;

public class ResultSet {


    private Object[] thisRow;
    @Getter
    private List<Class> metadata;
    private List<List<Object>> alldata;
    int rowCount = 1;

    public ResultSet(String data){

    }

    public boolean next() throws SqlException {
        return false;
    }

    public Object getObject(int index) throws SqlException {
        return thisRow[index];
    }


}
