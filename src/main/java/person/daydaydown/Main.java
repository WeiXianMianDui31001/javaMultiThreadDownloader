package person.daydaydown;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("请输入下载链接");
        String url = sc.nextLine();

        try {
            MultiThreadDownloader downloader = new MultiThreadDownloader();
            downloader.download(url, 4);
        } catch (IOException e) {
            System.out.println("下载失败" + e.getMessage());
        }
    }
}