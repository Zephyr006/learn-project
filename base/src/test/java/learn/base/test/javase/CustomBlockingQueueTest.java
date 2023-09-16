package learn.base.test.javase;

import learn.example.javase.SleepUtil;

import java.util.AbstractQueue;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

/**
 * @author Zephyr
 * @since 2021-12-18.
 */
public class CustomBlockingQueueTest {

    public static void main(String[] args) {
        Queue<Integer> queue = new CustomBlockingQueue<>(5);

        new Thread(() -> {
            Thread.currentThread().setPriority(10);
            SleepUtil.sleep(6000);
            Integer poll;
            while ((poll = queue.poll()) != null) {
                System.err.println("取到值 " + poll + "  current queue size = " + queue.size());
            }
        }).start();

        Thread.currentThread().setPriority(1);
        int count = 10;
        while (--count >= 0) {
            SleepUtil.sleep(300);
            queue.offer(new Random().nextInt(999));
            System.out.println("添加了一个元素，queueSize=" + queue.size());
        }
    }


    /**
     * 自己写一个阻塞队列，要点：
     * 1. 要有一个final的被锁对象（monitor），加锁和等待都必须在这个相同的对象上，因为加锁信息保存在对象的 markword 上
     * 2. wait和notify的使用基础是建立在一个共同的加锁对象的，所以第一件事是加锁（ synchronized(lock) ）
     * 3. ** wait方法应始终在循环中使用 **，因为要在wait方法结束等待后继续执行对应的业务处理，此处对应取出元素的操作
     */
    static class CustomBlockingQueue<E> extends AbstractQueue<E> {
        private int size = 0;
        private int maxSize = Integer.MAX_VALUE;
        private List<E> elements = new LinkedList<>();
        private final Object lock = new Object();

        public CustomBlockingQueue(int maxSize) {
            this.maxSize = maxSize;
        }

        @Override
        public Iterator<E> iterator() {
            return elements.iterator();
        }

        @Override
        public int size() {
            synchronized (lock) {
                return size;
            }
        }

        @Override
        public boolean offer(E e) {
            if (e == null) {
                throw new NullPointerException();
            }
            synchronized (lock) {
                // 超过规定的最大容量后  线程会阻塞等待，直到队列中的元素个数少于最大容量，才能继续添加
                while (size >= maxSize) {
                    try {
                        lock.wait();
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                }
                boolean add = elements.add(e);
                size++;
                lock.notifyAll();
                return add;
            }
        }

        @Override
        public E poll() {
            synchronized (lock) {
                while (true) {
                    if (size != 0) {
                        E e = elements.get(0);
                        elements.remove(0);
                        size--;
                        lock.notifyAll();
                        return e;
                    }
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        @Override
        public E peek() {
            synchronized (lock) {
                while (true) {
                    if (size != 0) {
                        return elements.get(0);
                    }
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}
