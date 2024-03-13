package org.mylog;

public enum LoggerLevel {

    OFF(Integer.MAX_VALUE),
    DEBUG(900),
    INFO(500),
    WARNING(300),
    ERROR(100),
    ALL(0);

    private final int level;

    LoggerLevel(int i) {
        this.level=i;
    }

    public int getLevel() {
        return level;
    }
}
