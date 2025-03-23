package org.mysql.sql;

import org.mysql.server.Request;
import org.mysql.server.RespType;
import org.mysql.server.Response;
import org.mysql.server.SqlType;

public class SqlHandler {

    public Response handle(Request request) {

        Response response = new Response();
        response.setSqlType(SqlType.UPDATE);
        response.setStatus(RespType.OK);
        response.setModify(0);
        return response;
    }

}
