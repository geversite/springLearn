package org.mysql.auth;

import org.mysql.server.*;

public class AuthHandler {

    public Response handle(Request request, ConnectionInfo connectionInfo) {

        Response response = new Response();
        String username = request.getRequestContent();
        String password = request.getParamValues()[0];
        response.setSqlType(SqlType.UPDATE);
        response.setStatus(RespType.OK);
        response.setModify(0);
        //if success
        connectionInfo.setDb(request.getParamValues()[1]);
        connectionInfo.setUser(request.getRequestContent());
        return response;
    }
}
