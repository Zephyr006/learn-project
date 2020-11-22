package learn.base.test;

import java.util.concurrent.Executors;

/**
 * @author Zephyr
 * @date 2020/7/2.
 */
public class ThreadPoolTest {

    public void testApi() {
        Executors.newCachedThreadPool();
        Executors.newFixedThreadPool(1);
        Executors.newSingleThreadExecutor();
        Executors.newScheduledThreadPool(1);

        Executors.newSingleThreadScheduledExecutor();
        Executors.newWorkStealingPool();
    }
}
