package learn.base.utils;

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

        //如果执行某一绝对时间(如2014/02/14 17:13:06)执行任务，此时可使用Timer
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
        return SingletonHolder.INNER;
    }

    /**
     * 单例模式：静态内部类不会随着外部类的初始化而初始化，他是要单独去加载和初始化的
     * 当第一次执行getInstance方法时，Inner类会被初始化。
     */
    private static class SingletonHolder {
        private static final CurrentTimeMillisClock INNER = new CurrentTimeMillisClock();
    }


    public static void main(String[] args) {
        long currentTimeMillis = CurrentTimeMillisClock.now();


        int times = 1234;

        long start = System.nanoTime();
        for (int i = 0; i < times; i++) {
            CurrentTimeMillisClock.now();
        }

        long start2 = System.nanoTime();
        for (int i = 0; i < times; i++) {
            System.currentTimeMillis();
        }
        long end = System.nanoTime();

        System.out.println("-----------------");

        long perCurr = (end - start2) / times;
        System.out.println(String.format("调用 %d 次，一次 System.currentTimeMillis 耗时 %d ns",times, perCurr));
        System.out.println(String.format("调用 %d 次，一次 CurrentTimeMillisClock.now 耗时 %d ns", times, (start2 - start)/times));

        long lessTake = (end - 2 * start2 + start) / times;
        System.out.println(String.format("平均每次调用比System.currentTimeMillis少耗时 %d ns，少耗时 %.3f%%",
                lessTake,  (lessTake*1.0 / perCurr)*100.0));

        System.out.println("-----------------");
    }
}
