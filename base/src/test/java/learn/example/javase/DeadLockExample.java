package learn.example.javase;

/**
 * 手写一个死锁的示例
 *
 * @author Zephyr
 * @since 2020-08-11.
 */
public class DeadLockExample {

    //必须有两个可以被加锁的对象才能产生死锁，只有一个不会产生死锁问题
    private final Object lock1 = new Object();
    private final Object lock2 = new Object();

    public static void main(String[] args) {
        new DeadLockExample().testDeadLock();
    }


    /**
     * 为了便于让两个线程分别锁住其中一个对象，
     * 一个线程锁住 lock1，然后一直等待 lock2，
     * 另一个线程锁住 lock2，然后一直等待 lock1，
     * 两个线程互相持有对方需要的锁，死锁产生
     */
    void testDeadLock() {
        Thread t1 = new Thread(() -> {
            synchronized (lock1) {
                System.out.println(Thread.currentThread().getName() + "获取到锁 lock1");
                SleepUtil.sleep(100);
                synchronized (lock2) {
                    System.out.println(Thread.currentThread().getName() + "获取到锁 lock2");
                    SleepUtil.sleep(100);
                }
            }
            System.out.println(Thread.currentThread().getName() + " finished");
        }, "thread1");
        Thread t2 = new Thread(() -> {
            synchronized (lock2) {
                System.out.println(Thread.currentThread().getName() + "获取到锁 lock2");
                SleepUtil.sleep(100);
                synchronized (lock1) {
                    System.out.println(Thread.currentThread().getName() + "获取到锁 lock1");
                    SleepUtil.sleep(100);
                }
            }
            System.out.println(Thread.currentThread().getName() + " finished");
        }, "thread2");


        //t1.start();
        //t2.start();
    }

}
