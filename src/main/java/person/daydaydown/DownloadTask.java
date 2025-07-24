package person.daydaydown;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;

public class DownloadTask implements Callable<Record> {
    private String url;
    private long start;
    private long end;
    private long cur;
    private File targetFile;
    private DownloadController controller;


    DownloadTask(File targetFile, String url, long start, long end, DownloadController controller, long cur) {
        this.targetFile = targetFile;
        this.url = url;
        this.start = start;
        this.end = end;
        this.controller = controller;
        this.cur = cur;
    }
    public Record call() {
        try {
            // 1. 设置Range头
            URL downloadURL = new URL(url);
            HttpURLConnection httpConn = (HttpURLConnection) downloadURL.openConnection();
            httpConn.setRequestProperty("Range", "bytes=" + cur + "-" + end);
            httpConn.connect();

            int responseCode = httpConn.getResponseCode();
            if(responseCode != HttpURLConnection.HTTP_OK && responseCode !=HttpURLConnection.HTTP_PARTIAL) {
                System.out.println("线程" + Thread.currentThread().getName() + "不支持断点下载:" + responseCode);
                return new Record(this.start, this.end, this.cur, Stat.NOT_DONE, url);
            }
            try(InputStream input = httpConn.getInputStream();
                RandomAccessFile raf = new RandomAccessFile(targetFile, "rw")){
                raf.seek(cur);
                byte[] buffer = new byte[1048576];
                int len;
                // 2. 使用HttpURLConnection下载该片段
                while((len = input.read(buffer)) != -1) {
                    // 每次写前检查是否要暂停
                    if(controller.checkPaused()) {
                        return new Record(this.start,this.end,this.cur,Stat.NOT_DONE,url);
                    }
                    // 3. 使用RandomAccessFile.seek写入文件对应位置
                    raf.write(buffer, 0, len);
                    cur += len;
                }
                System.out.println("线程" + Thread.currentThread().getName() + " 下载完成，范围：" + start + "-" + end);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Record(this.start,this.end,this.cur,Stat.DONE, url);
    }
}