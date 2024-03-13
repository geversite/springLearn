package org.mylog;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger{


    private static final String ANSI_RESET = "\033[0m";
    private static final String ANSI_GRAY = "\033[37m";  // 亮黑色通常看起来像灰色
    private static final String ANSI_WHITE = "";
//    private static final String ANSI_WHITE = "\033[97m";


    private static final String ANSI_YELLOW = "\033[93m";
    private static final String ANSI_RED = "\033[91m";
    private static OutputStream stream = System.out;

    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");



    private static int level = LoggerLevel.INFO.getLevel();

    private static final Logger logger = new Logger();

    private Logger(){}

    public static Logger getLogger(){
        return logger;
    }

    public void debug(String info){
        if (level>=LoggerLevel.DEBUG.getLevel()){
            print(info,"DEBUG",ANSI_GRAY);
        }
    }

    public void info(String info){
        if (level>=LoggerLevel.INFO.getLevel()){
            print(info,"INFO",ANSI_WHITE);
        }
    }
    public void warn(String info){
        if (level>=LoggerLevel.WARNING.getLevel()){
            print(info,"WARN",ANSI_YELLOW);
        }
    }
    public void error(String info){
        if (level>=LoggerLevel.ERROR.getLevel()){
            print(info,"ERROR",ANSI_RED);
        }
    }

    private void print(String s, String level, String color) {
        String timeNow = format.format(new Date());
        String thread = Thread.currentThread().getName();
        String msg = color+ "[" + timeNow + "]"+" [" +level + "] " + thread +": "+s +"\n"+ANSI_RESET;
        synchronized (stream){
            try {
                stream.write(msg.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static OutputStream getStream() {
        return stream;
    }

    public static void setStream(OutputStream stream) {
        Logger.stream = stream;
    }

    public static int getLevel() {
        return level;
    }

    public static void setLevel(int level) {
        Logger.level = level;
    }


}
