package com.zxb.test;


import org.myTomcat.core.MyTomcat;

public class myTest {
    public static void main(String[] args) throws Exception {
        new MyTomcat().start();
    }



}

//class Thread1 implements Runnable{
//
//    @Override
//    public void run() {
//        System.out.println(Thread.currentThread().getName());
//        try {
//            Connection connection = new DataSource.getConnection();
//            Thread.sleep(9000);
//            DataSource.close(connection);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//    }
//}