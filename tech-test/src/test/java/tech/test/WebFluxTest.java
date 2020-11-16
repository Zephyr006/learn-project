package tech.test;

import learn.base.utils.HttpClientUtil;
import org.junit.Test;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author Zephyr
 * @date 2020/9/2.
 */
public class WebFluxTest {
    CountDownLatch latch = new CountDownLatch(1000);
    Random random = new Random();

    @Test
    public void test () {
        String result = HttpClientUtil
                .doGet("http://localhost:8080/webflux/test/1", null);
        System.out.println(result);
    }


    @Test
    public void test2 () throws InterruptedException {
        Runnable runnable = () -> {
            for (int i = 0; i < 333; i++) {
                HttpClientUtil
                        .doGet("http://localhost:8080/springmvc/test/9000", null);
                try {
                    TimeUnit.MILLISECONDS.sleep(random.nextInt(3000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            latch.countDown();
        };

        long millis = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            Thread thread = new Thread(runnable);
            thread.start();
        }

        latch.await();
        System.err.println("here:" + (System.currentTimeMillis() - millis));
    }
}
