package org.myBatis.executor;

import org.myBatis.configuration.BoundSql;
import org.myBatis.configuration.Configuration;
import org.myBatis.configuration.MappedStatement;
import org.myBatis.utils.SqlParser;

import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class SimpleExecutor implements Executor{
    @Override
    public <E> List<E> query(Configuration configuration, MappedStatement statement, Object... params) throws Exception {
        Connection connection =null;
        PreparedStatement preparedStatement =null;
        ResultSet resultSet = null;
        List<Object> resultList = new ArrayList<>();
        try{
            connection = configuration.getDataSource().getConnection();
            String sql = statement.getSql();
            Parameter[] parameters = statement.getMethod().getParameters();
            BoundSql boundSql = SqlParser.parse(sql, parameters, params);

            preparedStatement = connection.prepareStatement(boundSql.getSql());
            for( int i=0; i<boundSql.getParams().size();i++)
                preparedStatement.setObject(i+1, boundSql.getParams().get(i));

            if(Arrays.asList("update","insert","delete").contains(statement.getSqlMethod())){
                List<Integer> updateResult = new ArrayList<>();
                updateResult.add(preparedStatement.executeUpdate());
                return (List<E>) updateResult;
            }else {
                resultSet = preparedStatement.executeQuery();
            }
            Type originReturnType = statement.getMethod().getGenericReturnType();
            Class<?> resultType = statement.getMethod().getReturnType();
            if (originReturnType instanceof ParameterizedType){
                if (Collection.class.isAssignableFrom(statement.getMethod().getReturnType())) {
                    Type innerType = ((ParameterizedType) originReturnType).getActualTypeArguments()[0];
                    if(innerType instanceof Class){
                        resultType = (Class<?>) innerType;
                    }
                }
            }
            while (resultSet.next()){
                ResultSetMetaData metaData = resultSet.getMetaData();
                Object o = resultType.getConstructor().newInstance();
//            Object o = new Object();
                for (int i = 1; i <= metaData.getColumnCount(); i++){
                    String name = metaData.getColumnName(i);
                    Object value = resultSet.getObject(name);
                    Field field = resultType.getDeclaredField(name);
                    field.setAccessible(true);
                    field.set(o, value);
                }
                resultList.add(o);
            }
        }finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close(); // 这里的 close 将连接返回到连接池
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println(connection.getClass());
        return (List<E>) resultList;

    }


}
