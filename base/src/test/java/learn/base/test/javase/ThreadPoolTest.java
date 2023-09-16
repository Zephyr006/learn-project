package learn.base.test.javase;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Zephyr
 * @since 2020-7-2.
 */
public class ThreadPoolTest {

    public void testApi() {
        Executors.newCachedThreadPool();
        Executors.newFixedThreadPool(1);
        Executors.newSingleThreadExecutor();
        Executors.newScheduledThreadPool(1);
        Executors.newCachedThreadPool(new ThreadFactory() {
            private final AtomicInteger nextId = new AtomicInteger(1);

            @Override
            public Thread newThread(Runnable r) {
                String name = "Worker-" + nextId.getAndIncrement();
                System.out.println(String.format("创建线程:%s", name));
                return new Thread(null, r, name, 0);
            }
        });

        Executors.newSingleThreadScheduledExecutor();
        Executors.newWorkStealingPool();
    }
}
