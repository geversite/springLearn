package org.myBatis.session;

import org.myBatis.configuration.MappedStatement;

import java.util.List;

public interface SqlSession {
    <E> List<E> selectList(MappedStatement statementId, Object... parameters) throws Exception;
    <E> E selectOne(MappedStatement statementId, Object... parameters) throws Exception;
    <E> E insert(MappedStatement statementId, Object... parameters) throws Exception;
    <E> E update(MappedStatement statementId, Object... parameters) throws Exception;
    <E> E delete(MappedStatement statementId, Object... parameters) throws Exception;
    <T> T getMapper(Class<?> mapperClass);

}
