package org.mysql.server;

public enum RequestType {

    AUTHORIZE("AUTHORIZE"),
    SQL("SQL"),
    EXIT("EXIT");




    String type;
    RequestType(String type) {
        this.type = type;
    }
}
