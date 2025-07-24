package person.daydaydown;

public class DownloadController {
    private volatile boolean paused = false;
    private volatile boolean stopped = false;
    public synchronized void pause() {
        paused = true;
    }

    public synchronized void resume() {
        paused = false;
        notifyAll(); // 唤醒等待的线程
    }
    public synchronized void stop() {
        stopped = true;
    }
    public synchronized boolean checkPaused() throws InterruptedException {
        if(stopped) {
            return true;
        }
        while (paused) {
            wait();
        }
        return false;
    }
}

