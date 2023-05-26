package learn.base.utils;

import org.slf4j.Logger;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程池状态相关讲解 { @link https://wenku.baidu.com/view/56b2becf9889680203d8ce2f0066f5335a816705.html }
 * @author Zephyr
 * @since 2022-10-27.
 */
public class ThreadPoolUtils {

    /**
     * 用于优雅地阻塞等待一个线程池关闭,比如 { @link https://blog.csdn.net/chenwiehuang/article/details/101532591 }
     * @param log 通过Logger可以判断出事哪里在执行关闭线程操作, 如果没有依赖logger的实现包则不会真的打印日志
     */
    public static void awaitShutdown(ExecutorService executorService, int waitMillis, Logger log) {
        if (executorService.isShutdown() || executorService.isTerminated()) {
            return;
        }
        long nowTime = CurrentTimeMillisClock.now();
        if (!executorService.isShutdown()) {
            executorService.shutdown();
        }
        try {
            if (executorService.isShutdown() && executorService.awaitTermination(waitMillis, TimeUnit.MILLISECONDS)) {
                if (log != null) {
                    log.info("线程池已被正常关闭,共耗时{}ms", CurrentTimeMillisClock.now() - nowTime);
                }
                return;
            }
        } catch (InterruptedException e) {
            if (log != null) {
                log.error("等待线程池关闭时被中断", e);
            }
        }
        if (log != null) {
            log.error("线程池中的任务没有执行完,数据状态可能产生异常...目前线程池中还有{}个未完成的任务,已丢弃", executorService.shutdownNow().size());
        }
    }


    /**
     * @param workQueue 尽量不要使用容量超过1000的任务队列
     * @param threadNamePrefix 如果线程名前缀用 "-"或"_" 结尾,则最终线程名会拼接自增数字,否则直接使用参数值作为线程名
     */
    public static ThreadPoolExecutor createThreadPool(int corePoolSize,
                                                      int maximumPoolSize,
                                                      long keepAliveSeconds,
                                                      BlockingQueue<Runnable> workQueue,
                                                      String threadNamePrefix) {
        return new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveSeconds, TimeUnit.SECONDS, workQueue,
            createThreadFactory(threadNamePrefix, false), new ThreadPoolExecutor.CallerRunsPolicy());
    }

    /**
     * @param workQueue 尽量不要使用容量超过1000的任务队列
     */
    public static ThreadPoolExecutor createThreadPool(int corePoolSize,
                                                      int maximumPoolSize,
                                                      long keepAliveSeconds,
                                                      BlockingQueue<Runnable> workQueue,
                                                      ThreadFactory threadFactory) {
        return new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveSeconds, TimeUnit.SECONDS, workQueue,
            threadFactory, new ThreadPoolExecutor.CallerRunsPolicy());
    }

    /**
     * @param threadNamePrefix 如果线程名前缀用 "-"或"_" 结尾,则最终线程名会拼接自增数字,否则直接使用参数值作为线程名
     */
    public static ThreadFactory createThreadFactory(String threadNamePrefix, boolean daemon) {
        return new ThreadFactory() {
            final AtomicInteger threadNumber = new AtomicInteger(1);

            @Override
            public Thread newThread(Runnable runnable) {
                SecurityManager s = System.getSecurityManager();
                ThreadGroup group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
                String threadName = (threadNamePrefix.endsWith("-") || threadNamePrefix.endsWith("_"))
                    ? threadNamePrefix + threadNumber.getAndIncrement() : threadNamePrefix;
                Thread thread = new Thread(group, runnable, threadName);
                thread.setDaemon(daemon);
                if (daemon)
                    thread.setPriority(Thread.MIN_PRIORITY + 1);
                else
                    thread.setPriority(Thread.NORM_PRIORITY);
                return thread;
            }
        };
    }
}
