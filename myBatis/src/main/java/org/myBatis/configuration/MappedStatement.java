package org.myBatis.configuration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MappedStatement {

    private String id;
    private String sqlMethod;
    private Method method;
    private String resultType;
    private String parameterType;
    private String sql;
}
