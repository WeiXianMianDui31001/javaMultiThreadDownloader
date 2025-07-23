package person.daydaydown;

public class DownloadController {
    private volatile boolean paused = false;

    public synchronized void pause() {
        paused = true;
    }

    public synchronized void resume() {
        paused = false;
        notifyAll(); // 唤醒等待的线程
    }

    public synchronized void checkPaused() throws InterruptedException {
        while (paused) {
            wait();
        }
    }
}

