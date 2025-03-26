package org.mysql.table;

import lombok.Getter;
import org.mysql.setting.Settings;
import org.mysql.sql.SqlCondition;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


/**
 * Table的结构：从table.json读取表格结构，表文件、日志文件
 * 表文件结构：第一块为sb，后面根据是否有pk为：纯数据块或索引加数据
 * 行结构：前面4字节头加序列化数据
 * 0.0：有效位
 * 0.1：删除位
 */
public class Table {

    private DataBase dataBase;
    String pk;
    RandomAccessFile tableFile;
    RandomAccessFile logFile;
    BufferedWriter logWriter;
    boolean index;
    int rowSize=HEADSIZE;//用于存储各类标志位
    @Getter
    List<String> columnNames = new ArrayList<>();
    @Getter
    List<Class> columnTypes = new ArrayList<>();
    static final int HEADSIZE= 4;
    static final int PAGESIZE = 4096;

    public Table(DataBase db, Object value) throws Exception {
        HashMap<String, Object> meta = (HashMap<String, Object>) value;
        this.dataBase = db;
        String tableFileName = (String) meta.get("tableFile");
        String logFileName = (String) meta.get("logFile");
        pk = (String) meta.get("pk");
        List<String> list = (List<String>) meta.get("columns");
        tableFile = getFile(tableFileName);
        logFile = getFile(logFileName);
        for (String data : list) {
            String name = data.split(":")[0].trim();
            String type = data.split(":")[1].trim();
            Class c = Types.toClass(type);
            this.columnNames.add(name);
            this.columnTypes.add(c);
            this.rowSize+=Types.getLength(name);
        }

        updateMeta(this.tableFile);
    }


    private RandomAccessFile getFile(String fileName) throws FileNotFoundException {
        File dbdir = new File(Settings.getInstance().getWorkDir());
        for (File file : dbdir.listFiles()) {
            if (file.getName().equals(fileName)) {
                return new RandomAccessFile(file, "rw");
            }
        }
        return null;
    }

    private byte[] getContent(RandomAccessFile file, int index) throws Exception {
        file.seek(index);
        byte[] buffer = new byte[PAGESIZE];
        file.read(buffer);
        return buffer;
    }

    private void updateMeta(RandomAccessFile file) throws Exception {
        byte[] bytes = getContent(file, 0);
        this.index = (bytes[0] & 0xFF)!=0;
    }

    public List<Row> getRows(SqlCondition condition) throws Exception {
        List<Row> result = new ArrayList<>();
        SqlCondition conditionpk = condition.getColumn(this.pk);
        if (conditionpk == null) {
            return searchRows(condition);
        } else {
            List<Row> rows = indexRows(conditionpk, 1);
            for (Row row : rows) {
                if (condition.match(row)){
                    result.add(row);
                }
            }
            return result;
        }
    }

    private List<Row> indexRows(SqlCondition conditionpk, int indexBlockNo) throws Exception {
        byte[] indexBlock = getContent(this.tableFile, indexBlockNo);
        Class indexType = Types.toClass(conditionpk.getConditionKey());
        int indexLength = Types.getLength(conditionpk.getConditionKey());
        int cnt = (Integer)Types.construct(indexBlock, Integer.class, 0);
        boolean leaf = (boolean)Types.construct(indexBlock, Boolean.class, 4);
        int targetPost = 0;
        for (int i = 0; i < cnt; i++) {
            //header + 指针0 + i * (对象加指针)
            int index = Types.getLength(Integer.class) + 1 + 4 + i*(indexLength+4);
            Object thisVal = Types.construct(indexBlock, indexType, index);
            int result = conditionpk.indexSearch(thisVal, indexType);
            if (result <= 0 ){
                if (leaf){
                     targetPost = Types.construct(indexBlock, Integer.class,index-4);
                     break;
                } else {
                    int leafPos = Types.construct(indexBlock, Integer.class,index-4)/PAGESIZE;
                    return indexRows(conditionpk, leafPos);
                }
            } else if (i == cnt-1){
                int leafPos = Types.construct(indexBlock, Integer.class,index+indexLength)/PAGESIZE;
                return indexRows(conditionpk, leafPos);
            }
        }
        int targetPage = targetPost/PAGESIZE;
        int pageBias = targetPost%PAGESIZE;
        byte[] buffer = getContent(this.tableFile, targetPage);
        byte[] temp = Arrays.copyOfRange(buffer, pageBias, this.rowSize);
        Row pkRow = new Row(temp, columnNames, columnTypes);
        List<Row> result = new ArrayList<>();
        result.add(pkRow);
        return result;

    }


    private List<Row> searchRows(SqlCondition condition) throws Exception {
        List<Row> result = new ArrayList<>();
        long pages = tableFile.length()/PAGESIZE;
        for (int i = 1; i < pages; i++) {
            byte[] buffer = getContent(this.tableFile, i);
            List<Row> temp = getRowsFromBlock(buffer);
            for (Row row : temp) {
                if (condition.match(row)){
                    result.add(row);
                }
            }
        }
        return result;
    }

    private List<Row> getRowsFromBlock(byte[] bytes) {
        List<Row> result = new ArrayList<>();
        try {
            int count  = PAGESIZE / this.rowSize;
            for (int i = 0; i < count; i++) {
                byte[] byteRow = new byte[this.rowSize];
                System.arraycopy(bytes, i * this.rowSize, byteRow, 0, byteRow.length);
                Row row = new Row(byteRow, this.columnNames, this.columnTypes);
                result.add(row);
            }
            return result;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public int addRow(Row row) {
        return 0;
    }


}
