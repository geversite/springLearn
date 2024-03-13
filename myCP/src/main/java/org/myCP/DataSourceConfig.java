package org.myCP;

import lombok.Data;
import lombok.ToString;
import org.mylog.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Properties;

@ToString
@Data
public class DataSourceConfig {

    private String driver;
    private String url;
    private String username;
    private String password;
    private Integer initSize = 3;
    private Integer maxSize = 6;
    private Boolean health = true;
    private Integer delay = 1000;
    private Integer period = 1000;
    private Integer timeout = 5000;
    private Integer waittime = 1000;
    private static Logger log = Logger.getLogger();

    public static final DataSourceConfig defaultConfig;

    static {
        defaultConfig = new DataSourceConfig();
        try {
            Properties properties = new Properties();
            InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties");
            properties.load(stream);
            for (Map.Entry<Object, Object> entry : properties.entrySet()) {
                String key = (String) entry.getKey();
                String value = (String) entry.getValue();
                key = key.split("\\.")[1];
                Field field = DataSourceConfig.class.getDeclaredField(key);
                field.setAccessible(true);
                Object obj = field.getType().getConstructor(String.class).newInstance(value);
                field.set(DataSourceConfig.defaultConfig,obj);
            }
        } catch (Exception e) {
            log.warn("db.properties not found or illegal");
        }
//        catch (NoSuchFieldException e) {
//            throw new RuntimeException(e);
//        } catch (IllegalAccessException e) {
//            throw new RuntimeException(e);
//        } catch (InstantiationException e) {
//            throw new RuntimeException(e);
//        } catch (InvocationTargetException e) {
//            throw new RuntimeException(e);
//        } catch (NoSuchMethodException e) {
//            throw new RuntimeException(e);
//        }

    }

    public DataSourceConfig() {

    }

}
