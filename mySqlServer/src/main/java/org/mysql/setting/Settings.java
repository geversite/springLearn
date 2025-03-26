package org.mysql.setting;

import lombok.Getter;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Properties;

public class Settings {


    @Getter
    private static final Settings instance;
    @Getter
    String workDir;

    static {
        instance = new Settings();
        Properties properties = new Properties();
        InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties");
        try {
            properties.load(stream);
            for (Map.Entry<Object, Object> entry : properties.entrySet()) {
                String key = (String) entry.getKey();
                String value = (String) entry.getValue();
                key = key.split("\\.")[1];
                Field field = Settings.class.getDeclaredField(key);
                field.setAccessible(true);
                Object obj = field.getType().getConstructor(String.class).newInstance(value);
                field.set(Settings.instance,obj);
            }
        } catch (IOException | NoSuchFieldException | InvocationTargetException | InstantiationException |
                 IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }


    private Settings() {

    }

}
