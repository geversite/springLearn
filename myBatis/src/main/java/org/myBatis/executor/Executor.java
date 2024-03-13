package org.myBatis.executor;

import org.myBatis.configuration.Configuration;
import org.myBatis.configuration.MappedStatement;

import java.util.List;

public interface Executor {
    <E> List<E> query(Configuration configuration, MappedStatement statement, Object... params) throws Exception;
}
