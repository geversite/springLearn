package org.myBatis.session;

import org.myBatis.configuration.Configuration;

public class DefaultSqlSessionFactory implements SqlSessionFactory{

    Configuration configuration;
    public DefaultSqlSessionFactory(Configuration configuration){
        this.configuration = configuration;
    }

    public SqlSession openSession(){
        return new DefaultSqlSession(configuration);
    }


}
