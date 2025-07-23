package person.daydaydown;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DownloadTask implements Runnable {
    private String url;
    private long start;
    private long end;
    private File targetFile;



    DownloadTask(File targetFile, String url, long start, long end) {
        this.targetFile = targetFile;
        this.url = url;
        this.start = start;
        this.end = end;
    }
    public void run() {
        try {
            // 1. 设置Range头
            URL downloadURL = new URL(url);
            HttpURLConnection httpConn = (HttpURLConnection) downloadURL.openConnection();
            httpConn.setRequestProperty("Range", "bytes=" + start + "-" + end);
            httpConn.connect();

            int responseCode = httpConn.getResponseCode();
            if(responseCode != HttpURLConnection.HTTP_OK && responseCode !=HttpURLConnection.HTTP_PARTIAL) {
                System.out.println("线程" + Thread.currentThread().getName() + "不支持断点下载:" + responseCode);
                return;
            }
            try(InputStream input = httpConn.getInputStream();
                RandomAccessFile raf = new RandomAccessFile(targetFile, "rw")){
                raf.seek(start);
                byte[] buffer = new byte[4096];
                int len;
                // 2. 使用HttpURLConnection下载该片段
                while((len = input.read(buffer)) != -1) {
                    // 3. 使用RandomAccessFile.seek写入文件对应位置
                    raf.write(buffer, 0, len);
                }
                System.out.println("线程" + Thread.currentThread().getName() + " 下载完成，范围：" + start + "-" + end);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}