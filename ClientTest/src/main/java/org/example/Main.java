package org.example;

import org.mySpring.boot.SpringApplication;
import org.mySpring.boot.SpringBootApplication;

// 按两次 ⇧ 打开“随处搜索”对话框并输入 `show whitespaces`，
// 然后按 Enter 键。现在，您可以在代码中看到空格字符。
@SpringBootApplication
public class Main extends SpringApplication {
    public static void main(String[] args) throws Exception {
        run(Main.class);
    }
}