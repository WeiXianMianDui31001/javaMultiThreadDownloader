package person.daydaydown;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    final static int THREAD_COUNT = 8;
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("请输入下载链接");
        String url = sc.nextLine();

        try {
            MultiThreadDownloader downloader = new MultiThreadDownloader();
            downloader.download(url, THREAD_COUNT);


            // 新线程监听用户输入控制下载
            new Thread(() -> {
                while (true) {
                    System.out.println("输入 pause 暂停，resume 恢复，exit 退出：");
                    String cmd = sc.nextLine();
                    if ("pause".equalsIgnoreCase(cmd)) {
                        downloader.pause();
                        System.out.println("已暂停");
                    } else if ("resume".equalsIgnoreCase(cmd)) {
                        downloader.resume();
                        System.out.println("已恢复");
                    } else if ("exit".equalsIgnoreCase(cmd)) {
                        System.exit(0);
                    }
                }
            }).start();
        } catch (IOException e) {
            System.out.println("下载失败" + e.getMessage());
        }
    }
}