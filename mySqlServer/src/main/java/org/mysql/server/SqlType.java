package org.mysql.server;

public enum SqlType {
    QUERY("QUERY"),
    UPDATE("UPDATE");


    String type;
    SqlType(String type) {
        this.type = type;
    }
}
