package test.multi_thread;

import org.testng.annotations.Test;

import java.util.Map;
import java.util.concurrent.Callable;

/**
 * @author Zephyr
 * @date 2020/6/19.
 */
public class ThreadTest {

    /**
     *返回该线程的状态。 此方法被设计以用于监测的系统状态，而不是同步控制。
     */
    @Test
    public void testThread() throws InterruptedException {
        Callable<Map> callable = new Callable<Map>() {
            @Override
            public Map call() throws Exception {
                return null;
            }
        };
        Runnable runnable = () -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.err.println(123);
        };


        Thread thread = new Thread(runnable, "sub_thread");
        thread.start();

        System.out.println(thread.getState());
        thread.join();
        System.out.println(thread.getState());
        System.err.println(Thread.currentThread().getState());
    }
}
