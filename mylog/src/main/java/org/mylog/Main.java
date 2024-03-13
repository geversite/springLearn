package org.mylog;

// 按两次 ⇧ 打开“随处搜索”对话框并输入 `show whitespaces`，
// 然后按 Enter 键。现在，您可以在代码中看到空格字符。
public class Main {
    public static void main(String[] args) {
        // 当文本光标位于高亮显示的文本处时按 ⌥⏎，
        // 可查看 IntelliJ IDEA 对于如何修正该问题的建议。
        Logger log = Logger.getLogger();
        log.debug("debugmdg");
        log.info("information");
        log.warn("warning!!!");
        log.error("error!!");
    }
}