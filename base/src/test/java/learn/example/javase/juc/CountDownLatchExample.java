package learn.example.javase.juc;

import learn.example.javase.SleepUtil;

import java.util.Random;
import java.util.concurrent.CountDownLatch;

/**
 * CountDownLatch是通过一个计数器来实现的，计数器的初始化值为线程的数量。
 * 每当一个线程完成了自己的任务后，计数器的值就相应的 减1。
 * 当计数器到达0时，表示所有的线程都已完成任务，然后在闭锁上等待的线程就可以恢复执行任务。
 *
 * @author Zephyr
 * @since 2020-8-11.
 */
public class CountDownLatchExample {

    CountDownLatch latch = new CountDownLatch(3);
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                System.out.println(Thread.currentThread().getName() + " run start. ");
                SleepUtil.sleep(new Random().nextInt(6666));
                System.out.println(Thread.currentThread().getName() + " run end. ");
            } finally {
                latch.countDown();
            }
        }
    };

    public void testCountDownLatch() throws InterruptedException {
        Thread thread1 = new Thread(runnable, "t1");
        Thread thread2 = new Thread(runnable, "t2");
        Thread thread3 = new Thread(runnable, "t3");

        thread1.start();
        thread2.start();
        thread3.start();


        System.out.println("Main thread wait for other thread finish.");
        latch.await();
        System.out.println("All thread are finished. ");
        System.out.println("Main thread exit.");
    }


    public static void main(String[] args) {
        try {
            new CountDownLatchExample().testCountDownLatch();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
