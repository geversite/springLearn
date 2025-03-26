package org.mysql.table;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Types {

    public static Class toClass(String type) {
        switch (type){
            case "integet":
                return Integer.class;
            case "long":
                return Long.class;
            case "float":
                return Float.class;
            case "double":
                return Double.class;
            case "boolean":
                return Boolean.class;
            case "byte":
                return Byte.class;
            case "short":
                return Short.class;
            case "character":
                return Character.class;
            case "date":
                return Date.class;
            case "string":
                return String.class;
        }
        return null;
    }

    private static int bytesToInt(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getInt();
    }

    private static long bytesToLong(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getLong();
    }

    private static float bytesToFloat(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getFloat();
    }

    private static double bytesToDouble(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getDouble();
    }

    private static short bytesToShort(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getShort();
    }

    private static char bytesToChar(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getChar();
    }

    private static boolean bytesToBoolean(byte[] bytes) {
        return bytes[0] != 0;
    }

    private static String bytesToString(byte[] bytes) {
        return new String(bytes);
    }

    private static Object bytesToDate(byte[] bytes) {
        Long date = bytesToLong(bytes);
        return new Date(date);
    }

    public static int getLength(Class clazz) {
        return getLength(clazz.getSimpleName().toLowerCase());
    }

    public static int getLength(String type) {
        switch (type){
            case "integet":
                return 4;
            case "long":
                return 8;
            case "float":
                return 4;
            case "double":
                return 8;
            case "boolean":
                return 1;
            case "byte":
                return 1;
            case "short":
                return 2;
            case "character":
                return 1;
            case "date":
                return 8;
            case "string":
                return 64;
        }
        return 0;
    }

    public static <T> T construct(byte[] byteRow, Class<T> clazz, int index) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        int length = getLength(clazz);
        byte[] bytes = new byte[length];
        System.arraycopy(byteRow, index, bytes, 0, length);
        Method method = Types.class.getDeclaredMethod("bytesTo"+clazz.getSimpleName(), byte[].class);
        return (T)method.invoke(null, bytes);
    }

    public static String toStr(Object obj) {
        if (obj == null) {
            return "null";
        }
        if (obj instanceof Date) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            String formattedDate = sdf.format((Date)obj);
            return formattedDate;
        }
        return null;
    }

}
