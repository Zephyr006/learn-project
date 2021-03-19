package learn.base.test;

import learn.example.javase.SleepUtil;
import org.junit.Test;

import java.time.LocalTime;
import java.util.concurrent.CountDownLatch;

/**
 * @author Zephyr
 * @date 2021/2/22.
 */
public class CountDownLatchTest {

    static CountDownLatch countDownLatch = new CountDownLatch(1);

    @Test
    public void testCountDownLatch() throws InterruptedException {
        Runnable runnable = () -> {
            System.out.println(Thread.currentThread().getName());
            SleepUtil.sleep(4000);
            System.out.println(LocalTime.now());
            countDownLatch.countDown();
        };

        new Thread(runnable).start();
        countDownLatch.await();
    }



}
