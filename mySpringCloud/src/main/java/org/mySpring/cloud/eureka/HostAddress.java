package org.mySpring.cloud.eureka;

import lombok.Data;

@Data
public class HostAddress {

    public HostAddress(String host){
        ip=host.split(":")[0];
        port = host.split(":")[1];
    }

    public HostAddress(String ip, String port){
        this.ip=ip;
        this.port = port;
    }

    String ip;
    String port;
}
