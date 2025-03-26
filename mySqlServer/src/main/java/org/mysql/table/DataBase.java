package org.mysql.table;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.Getter;
import org.mysql.setting.Settings;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataBase {

    String dbName;
    Map<String, Table> tables = new HashMap<>();
    public static Map<String, DataBase> DataBases = LoadDataBases();

    public DataBase(Path tablesJsonPath) throws Exception {
        dbName = tablesJsonPath.getParent().getFileName().toString();
        String content = new String(Files.readAllBytes(tablesJsonPath));
        HashMap<String, Object> map = new Gson().fromJson(content, HashMap.class);
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            tables.put(key, new Table(this, value));
        }

    }

    public Table getTable(String tableName) {
        return tables.get(tableName);
    }

    public static Map<String, DataBase> LoadDataBases() {
        Map<String, DataBase> dbs = new HashMap<String, DataBase>();
        String basedir = Settings.getInstance().getWorkDir();
        Path basePath = Paths.get(basedir);

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(basePath)) {
            for (Path entry : stream) {
                if (Files.isDirectory(entry)) {
                    Path tablesJsonPath = entry.resolve("tables.json");
                    if (Files.exists(tablesJsonPath)) {
                        dbs.put(entry.getFileName().toString(), new DataBase(tablesJsonPath));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return dbs;
    }




}
