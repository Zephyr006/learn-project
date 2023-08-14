package learn.example.multithread;

import learn.base.utils.TripleFunction;
import org.apache.commons.collections4.ListUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Zephyr
 * @date 2023/7/3.
 */
public class AlternateExecuteTest {

    @Test
    public void testWaitNotify() throws InterruptedException {
        final Object lock = new Object();
        final int max = 100;
        CountDownLatch latch = new CountDownLatch(2);
        List<Integer> result = new ArrayList<>();
        List<Integer> target = IntStream.range(0, max).boxed().collect(Collectors.toList());

        Function<Predicate<Integer>, Runnable> taskFactory = predicate ->
            () -> {
                // wait / notify 方法的调用必须配合synchronized(lock)使用,否则抛出 IllegalMonitorStateException 异常
                synchronized (lock) {
                    for (int i = 0; i < max; i++) {
                        // 如果是先从奇数开始输出,把这里的test结果取反!即可
                        if (predicate.test(i)) {
                            result.add(i);
                            lock.notify();
                        } else {
                            try {
                                lock.wait();
                            } catch (InterruptedException ignore) {
                            }
                        }
                    }
                }
                latch.countDown();
            };

        new Thread(taskFactory.apply(i -> i % 2 != 0), "T1").start();
        new Thread(taskFactory.apply(i -> i % 2 == 0), "T2").start();
        // Thread.sleep(666);
        latch.await();
        assert ListUtils.isEqualList(result, target);
    }

    /*
    @Test
    public void testExecAlternatelyBySynchronized() throws InterruptedException {
        // 使用布尔变量对打印顺序进行控制，true表示轮到当前线程打印
        boolean startA = true;
        boolean startB = false;
        boolean startC = false;
        // 作为锁对象
        final Object o = new Object();
        // A线程
        new Thread(() -> {
            synchronized (o) {
                for (int i = 0; i < 10; ) {
                    if (startA) {
                        // 代表轮到当前线程打印
                        System.out.print(Thread.currentThread().getName());
                        // 下一个轮到B打印，所以把startB置为true，其它为false
                        startA = false;
                        startB = true;
                        startC = false;
                        // 唤醒其他线程
                        o.notifyAll();
                        // 在这里对i进行增加操作
                        i++;
                    } else {
                        // 说明没有轮到当前线程打印，继续wait
                        try {
                            o.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }, "A").start();
        // B线程
        new Thread(() -> {
            synchronized (o) {
                for (int i = 0; i < 10; ) {
                    if (startB) {
                        System.out.print(Thread.currentThread().getName());
                        startA = false;
                        startB = false;
                        startC = true;
                        o.notifyAll();
                        i++;
                    } else {
                        try {
                            o.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }, "B").start();
        // C线程
        new Thread(() -> {
            synchronized (o) {
                for (int i = 0; i < 10; ) {
                    if (startC) {
                        System.out.print(Thread.currentThread().getName());
                        startA = true;
                        startB = false;
                        startC = false;
                        o.notifyAll();
                        i++;
                    } else {
                        try {
                            o.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }, "C").start();
    }
     */

    /**
     * 3个线程顺序的打印ABC,打印3轮,
     */
    @Test
    public void testExecAlternatelyByCondition() throws InterruptedException {
        String str = "ABC";
        List<String> target = IntStream.range(0, 3*3)
            .mapToObj(i -> String.valueOf(str.charAt(i % str.length())))
            .collect(Collectors.toList());
        List<String> result = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(3);

        Lock lock = new ReentrantLock();
        Condition aCondition = lock.newCondition();
        Condition bCondition = lock.newCondition();
        Condition cCondition = lock.newCondition();

        TripleFunction<String, Condition, Condition, Runnable> threadFactory = (input, signalCondition, waitCondition) -> {
            return (Runnable) () -> {
                try {
                    lock.lock();
                    for (int i = 0; i < 3; i++) {
                        result.add(input);
                        // 本线程已经执行完,叫醒下一个线程
                        signalCondition.signal();
                        // 本线程阻塞等待
                        waitCondition.await();
                    }
                    // 这里有个坑，要记得在循环之后调用signal()，否则线程可能会一直处于wait状态，导致程序无法结束
                    signalCondition.signal();
                    latch.countDown();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }
            };
        };
        new Thread(threadFactory.apply("A", bCondition, aCondition), "thread-A").start();
        new Thread(threadFactory.apply("B", cCondition, bCondition), "thread-B").start();
        new Thread(threadFactory.apply("C", aCondition, cCondition), "thread-C").start();

        latch.await();
        assert ListUtils.isEqualList(result, target);
    }


}
