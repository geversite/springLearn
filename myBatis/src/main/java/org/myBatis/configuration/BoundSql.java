package org.myBatis.configuration;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BoundSql {

    private String sql;
    private List<String> paramNames = new ArrayList<>();
    private List<Object> params = new ArrayList<>();
}
