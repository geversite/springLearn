package org.myBatis.session;

import org.myBatis.configuration.Configuration;
import org.myBatis.configuration.MappedStatement;
import org.myBatis.executor.*;

import java.lang.reflect.*;
import java.util.List;

public class DefaultSqlSession implements SqlSession{
    public Configuration getConfiguration() {
        return configuration;
    }

    private final Configuration configuration;

    public DefaultSqlSession(Configuration configuration){
        this.configuration = configuration;
    }

    @Override
    public <E> List<E> selectList(MappedStatement statement, Object... parameters) throws Exception {
        Executor executor = new SimpleExecutor();
        List<Object> query = executor.query(configuration, statement, parameters);
        return (List<E>) query;
    }

    @Override
    public <E> E selectOne(MappedStatement statement, Object... parameters) throws Exception {
        List<Object> list = selectList(statement, parameters);
        if(list.size()==1){
            return (E) list.get(0);
        }else if (list.size()>1){
            throw new RuntimeException("Result set has more than 1 element");
        }else {
            return null;
        }
    }

    @Override
    public <E> E insert(MappedStatement statement, Object... parameters) throws Exception {
        List<Object> list = selectList(statement, parameters);
        return (E) list.get(0);
    }

    @Override
    public <E> E update(MappedStatement statement, Object... parameters) throws Exception {
        List<Object> list = selectList(statement, parameters);
        return (E) list.get(0);
    }

    @Override
    public <E> E delete(MappedStatement statement, Object... parameters) throws Exception {
        List<Object> list = selectList(statement, parameters);
        return (E) list.get(0);
    }

    @Override
    public <T> T getMapper(Class<?> mapperClass) {
        Object obj = Proxy.newProxyInstance(mapperClass.getClassLoader(), new Class[]{mapperClass}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                String name = method.getName();
                switch (name) {
                    case "toString":
                        return proxy.getClass().getName() + "@" + System.identityHashCode(proxy);
                    case "equals":
                        return args[0] == proxy;
                    case "hashCode":
                        return System.identityHashCode(proxy);
                }

                MappedStatement statement = new MappedStatement();
                String id = mapperClass.getName() + "." + name;
                statement.setId(id);
                statement.setMethod(method);
                Type returnType = null;
                if (method.isAnnotationPresent(Select.class)) {
                    String action = method.getAnnotation(Select.class).value();
                    statement.setSqlMethod("select");
                    statement.setSql(action);
                    statement.setResultType(method.getGenericReturnType().getTypeName());
                } else if (method.isAnnotationPresent(Update.class)) {
                    String action = method.getAnnotation(Update.class).value();
                    statement.setSqlMethod("update");
                    statement.setSql(action);
                    statement.setResultType(method.getGenericReturnType().getTypeName());
                } else if (method.isAnnotationPresent(Insert.class)) {
                    String action = method.getAnnotation(Insert.class).value();
                    statement.setSqlMethod("insert");
                    statement.setSql(action);
                    statement.setResultType(method.getGenericReturnType().getTypeName());
                } else if (method.isAnnotationPresent(Delete.class)) {
                    String action = method.getAnnotation(Delete.class).value();
                    statement.setSqlMethod("delete");
                    statement.setSql(action);
                    statement.setResultType(method.getGenericReturnType().getTypeName());
                } else {
                    statement = configuration.getMappedStatements().get(id);
                    statement.setResultType(method.getGenericReturnType().getTypeName());
                }
                if (statement.getSqlMethod().equals("update")) {
                    return update(statement, args);
                } else if (statement.getSqlMethod().equals("insert")) {
                    return insert(statement, args);
                } else if (statement.getSqlMethod().equals("delete")) {
                    return delete(statement, args);
                } else if (statement.getMethod().getGenericReturnType() instanceof ParameterizedType) {
                    return selectList(statement, args);
                } else {
                    return selectOne(statement, args);
                }
            }
        });
        return (T) obj;
    }
}
