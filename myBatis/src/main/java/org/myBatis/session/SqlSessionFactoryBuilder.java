package org.myBatis.session;

import org.myBatis.configuration.Configuration;
import org.myBatis.configuration.XmlConfigBuilder;

import java.io.InputStream;

public class SqlSessionFactoryBuilder {

    public SqlSessionFactory build(Configuration config){
        return new DefaultSqlSessionFactory(config);
    }

    public SqlSessionFactory build(InputStream stream) throws Exception {
        XmlConfigBuilder builder = new XmlConfigBuilder();
        Configuration config = builder.parse(stream);
        return new DefaultSqlSessionFactory(config);
    }
}
