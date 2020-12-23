package learn.light4j.util;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * System.currentTimeMillis() Cache
 *
 * @author Zephyr
 * @date 2020/12/18.
 */
public class CurrentTimeMillisClock {
    private volatile long now;

    private CurrentTimeMillisClock() {
        this.now = System.currentTimeMillis();
        scheduleTick();
    }

    private void scheduleTick() {
        // JDK1.5之后，计划任务建议使用ScheduledThreadPoolExecutor
        new ScheduledThreadPoolExecutor(1, runnable -> {
            Thread thread = new Thread(runnable, "current-time-millis");
            // thread.setDaemon(true)必须在thread.start()之前设置，否则会抛出一个IllegalThreadStateException异常
            thread.setDaemon(true);
            return thread;
        }).scheduleAtFixedRate(() -> {
            now = System.currentTimeMillis();
        }, 1, 1, TimeUnit.MILLISECONDS);

        //如果执行某一绝对时间(如2012/12/12 12:12:12)执行任务，此时可使用Timer
        //new Timer("timer-current-time-millis", true).scheduleAtFixedRate(new TimerTask() {
        //    @Override
        //    public void run() {
        //        now = System.currentTimeMillis();
        //    }
        //}, 1, 1);
    }

    public static long now() {
        return getInstance().now;
    }

    public static CurrentTimeMillisClock getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static final CurrentTimeMillisClock INSTANCE = new CurrentTimeMillisClock();
    }

    public static void main(String[] args) {
        long currentTimeMillis = CurrentTimeMillisClock.now();
    }
}
