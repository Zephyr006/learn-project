package learn.example.javase.juc;

import learn.base.utils.StopWatch;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @author Zephyr
 * @date 2021/4/11.
 */
public class ExecutorServiceExample {
    Callable<Integer> action = () -> {
        try {
            TimeUnit.SECONDS.sleep(3);
            String threadName = Thread.currentThread().getName();
            System.out.println(threadName);
            return Integer.valueOf(threadName.substring(threadName.length() - 1));
        } catch (InterruptedException e) {
            e.printStackTrace();
            return 0;
        }
    };

    /**
     * awaitTermination会阻塞式的等待线程池完成shutdown动作，直到超时为止
     */
    @Test
    public void testAwaitTermination() {
        StopWatch stopWatch = StopWatch.createAndStart(this.getClass().getName());
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        Callable<Integer> action = () -> {
            try {
                TimeUnit.SECONDS.sleep(3);
                String threadName = Thread.currentThread().getName();
                System.out.println(threadName);
                return Integer.valueOf(threadName.substring(threadName.length() - 1));
            } catch (InterruptedException e) {
                e.printStackTrace();
                return 0;
            }
        };

        executorService.submit(action);
        executorService.submit(action);
        executorService.submit(action);
        executorService.shutdown();
        try {
            executorService.awaitTermination(9999, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(stopWatch.stopAndPrint());
    }

    /**
     * executorService.invokeAll() 方法会阻塞式的等待这批任务执行完毕，并且ExecutionException（或其他异常，取决于具体实现）将被忽略
     */
    @Test
    public void testInvokeAll() throws InterruptedException {
        StopWatch stopWatch = StopWatch.createAndStart(this.getClass().getName());
        ExecutorService executorService = Executors.newWorkStealingPool(2);


        List<Future<Integer>> futureList = executorService.invokeAll(Arrays.asList(action, action, action));
        List<Integer> resultList = futureList.stream().map(future -> {
            try {
                return future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            return 0;
        }).collect(Collectors.toList());
        resultList.forEach(System.out::println);
        executorService.shutdown();
        //try {
        //    executorService.awaitTermination(9999, TimeUnit.SECONDS);
        //} catch (InterruptedException e) {
        //    e.printStackTrace();
        //}
        System.out.println(stopWatch.stopAndPrint());
    }
}
