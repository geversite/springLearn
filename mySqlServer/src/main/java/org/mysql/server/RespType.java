package org.mysql.server;

public enum RespType {
    ERROR("ERR"),
    OK("OK");




    String type;
    RespType(String type) {
        this.type = type;
    }
}
