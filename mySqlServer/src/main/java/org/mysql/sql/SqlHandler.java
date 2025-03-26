package org.mysql.sql;

import org.mysql.server.*;

public class SqlHandler {

    public Response handle(Request request, ConnectionInfo info) throws Exception {
        SqlMeta meta = new Parser(request.getRequestContent()).parse();
        Executor executor = new Executor(meta, info);
        Response response = executor.execute();

        return response;
    }

}
