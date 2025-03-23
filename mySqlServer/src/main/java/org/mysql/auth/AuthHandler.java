package org.mysql.auth;

import org.mysql.server.Request;
import org.mysql.server.RespType;
import org.mysql.server.Response;
import org.mysql.server.SqlType;

public class AuthHandler {

    public Response handle(Request request) {
        Response response = new Response();
        String username = request.getRequestContent();
        String password = request.getParamValues()[0];
        response.setSqlType(SqlType.UPDATE);
        response.setStatus(RespType.OK);
        response.setModify(0);
        return response;
    }
}
