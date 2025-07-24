package person.daydaydown;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Main {
    final static int THREAD_COUNT = 8;
    static List<Record> records = new ArrayList<Record>();
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Scanner sc = new Scanner(System.in);
        String url = "";
        String choice = "no";
        File dat = new File("records.dat");
        if(dat.exists()){
            System.out.println("检测到有下载记录，是否继续下载");
            choice = sc.nextLine();
        }
        if(choice.equals("no")) {
            System.out.println("请输入下载链接");
            url = sc.nextLine();
        } else {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(dat))) {
                records = (List<Record>) ois.readObject();
            }
        }
        try {
            MultiThreadDownloader downloader = new MultiThreadDownloader();
            if(choice == "no") {
                downloader.download(url, THREAD_COUNT);
            }
            else {
                downloader.download(records);
            }


            // 新线程监听用户输入控制下载
            new Thread(() -> {
                while (true) {
                    System.out.println("输入 pause 暂停，resume 恢复，exit 退出， save 保存并退出:");
                    String cmd = sc.nextLine();
                    if ("pause".equalsIgnoreCase(cmd)) {
                        downloader.pause();
                        System.out.println("已暂停");
                    } else if ("resume".equalsIgnoreCase(cmd)) {
                        downloader.resume();
                        System.out.println("已恢复");
                    } else if ("exit".equalsIgnoreCase(cmd)) {
                        for (Future<Record> record : downloader.records) {
                            try {
                                records.add(record.get());
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            } catch (ExecutionException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        long cnt = records.stream()
                                .filter(record -> record.state == Stat.DONE)
                                .count();
                        if(cnt == records.size()){
                            //不知道为什么没删除成功
                            dat.delete();
                        }
                        System.exit(0);
                    }
                    else if("save".equalsIgnoreCase(cmd)) {
                        downloader.stop();
                        records.clear();
                        for (Future<Record> record : downloader.records) {
                            try {
                                records.add(record.get());
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            } catch (ExecutionException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("records.dat"))){
                            oos.writeObject(records);
                        } catch(Exception e) {
                            System.out.println(e);
                        }
                        System.exit(0);
                    }
                }
            }).start();
        } catch (IOException e) {
            System.out.println("下载失败" + e.getMessage());
        }
    }
}