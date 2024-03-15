package org.myTomcat.config;

import lombok.Data;

@Data
public class ServerConfig {

    Integer port = 8080;
    Integer thread = 3;
    Integer maxThread = 6;
}
