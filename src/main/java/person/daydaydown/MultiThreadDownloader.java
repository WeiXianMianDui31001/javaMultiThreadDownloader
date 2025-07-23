package person.daydaydown;

import com.sun.net.httpserver.HttpContext;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiThreadDownloader {
    int contentSize;
    String contentType;
    String contentName;
    List<DownloadTask> task = new ArrayList<DownloadTask>();
    ExecutorService pool = Executors.newCachedThreadPool();


    public void download(String fileURL, int threadCount) throws IOException {
        // 1. 获取文件大小,文件名字
        URL url = new URL(fileURL);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        int responseCode = httpConn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            contentSize = httpConn.getContentLength();
            contentType = httpConn.getContentType();
            contentName = httpConn.getHeaderField("Content-Disposition");
            if(contentName == null) {
                contentName = fileURL.substring(fileURL.lastIndexOf("/") + 1);
            }
        } else {
            System.out.println("服务器返回非OK状态" + responseCode);
        }
        // 2. 创建下载的文件，计算每段范围，分配线程
        File file = new File(contentName);
        int pieceSize = contentSize / threadCount;
        int beginIndex = 0;
        for(int i = 0; i < threadCount; i++) {
            int endIndex;
            if(i == threadCount - 1) {
                endIndex = contentSize - 1;
            } else {
                endIndex = beginIndex + pieceSize - 1;
            }
            task.add(new DownloadTask(file ,fileURL ,beginIndex ,endIndex));
            beginIndex = endIndex + 1;
        }
        // 3. 启动线程池并等待所有线程完成
        for(DownloadTask task : task) {
            pool.execute(task);
        }

        // 4. 结束http连接,关闭线程池
        httpConn.disconnect();
        pool.shutdown();
    }
}