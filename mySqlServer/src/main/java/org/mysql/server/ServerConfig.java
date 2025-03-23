package org.mysql.server;

import lombok.Data;

@Data
public class ServerConfig {
    int port = 3307;
    int thread = 3;
    int maxThread = 6;
}
