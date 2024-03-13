package org.myBatis.configuration;

import lombok.Data;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class Configuration {

    private DataSource dataSource;
    private Map<String, MappedStatement> mappedStatements = new ConcurrentHashMap<>();
}
