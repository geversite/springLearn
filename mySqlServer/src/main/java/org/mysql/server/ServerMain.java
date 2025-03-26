package org.mysql.server;

import org.mysql.setting.Settings;

public class ServerMain {

    public static void main(String[] args) {
        init();
        new Server().start();
    }

    private static void init() {
        Settings.getInstance();
    }
}
