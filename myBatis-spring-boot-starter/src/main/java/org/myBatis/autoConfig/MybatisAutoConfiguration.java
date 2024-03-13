package org.myBatis.autoConfig;


import org.myBatis.session.SqlSession;
import org.myBatis.session.SqlSessionFactoryBuilder;
import org.myCP.DataSource;
import org.myCP.DataSourceConfig;
import org.mySpring.annotation.Bean;
import org.mySpring.annotation.Configuration;
import org.mySpring.annotation.Import;
import org.mySpring.boot.Conditional;
import org.mySpring.boot.Environment;

import java.lang.reflect.Field;
import java.util.Map;

@Configuration
@Import(MapperScannerRegistrar.class)
public class MybatisAutoConfiguration {

    @Bean
    @Conditional(EnableMybatis.class)
    public SqlSession getSqlSession(){
        Environment environment = Environment.getEnvironment();
        DataSourceConfig config = getCPConfig(environment);
        DataSource dataSource = new DataSource(config);
        org.myBatis.configuration.Configuration configuration = new org.myBatis.configuration.Configuration();
        configuration.setDataSource(dataSource);
        SqlSession session = new SqlSessionFactoryBuilder().build(configuration).openSession();
        return session;
    }


    private DataSourceConfig getCPConfig(Environment environment) {
        DataSourceConfig config = new DataSourceConfig();
        for (Map.Entry<String, String> entry : environment.getData().entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if(key.startsWith("jdbc.")){
                key = key.substring(5);
                try {
                    Field field = config.getClass().getDeclaredField(key);
                    field.setAccessible(true);
                    Object obj = field.getType().getConstructor(String.class).newInstance(value);
                    field.set(config, obj);
                } catch (Exception ignored) {
                }
            }
        }
        return config;
    }
}
