### ThreadPoolExecutor
使用（线程）池中的线程执行提交的task，通常使用`Executors`的工厂方法配置

**构造方法：**
```java
public ThreadPoolExecutor(int corePoolSize, int maximumPoolSize,
            long keepAliveTime, TimeUnit timeUnit, BlockingQueue<Runnable> workQueue,
            ThreadFactory threadFactory, RejectedExecutionHandler handler){}

- corePoolSize 核心线程数：是线程池存活线程的最低数量（如果设置了`allowCoreThreadTimeOut`，则最低线程数为0）
- maximumPoolSize 线程池的最大线程数。注意：实际最大值受容量限制（CAPACITY = `(1 << (Integer.SIZE - 3)) - 1`）
- keepAliveTime 线程存活时间：当线程池的存活线程数大于核心线程数时，这是多余的空闲线程等待新任务的最长等待时间（等待超时将被结束）
- timeUnit      线程存活时间的时间单位
- workQueue     在任务被执行之前用于保留任务的队列。此队列将仅包含`execute`方法提交的`Runnable`任务
- threadFactory 执行程序*创建新线程*时要使用的工厂
- rejectedExecutionHandler 当执行被阻塞时(达到线程界限和队列容量)要使用的处理程序
```
**常用的预定义线程池：**

- Executors.newFixedThreadPool(nThreads);
> 适用场景：可用于Web服务瞬时削峰，但需注意长时间持续高峰情况造成的队列阻塞。
> corePoolSize与maximumPoolSize相等，即其线程全为核心线程，是一个固定大小的线程池；
> workQueue 为LinkedBlockingQueue（无界阻塞队列），队列最大值为Integer.MAX_VALUE。如果任务提交速度持续大余任务处理速度，会造成队列大量阻塞。因为队列很大，很有可能在拒绝策略前，内存溢出。
> FixedThreadPool的任务执行是无序的；
- Executors.newCachedThreadPool();
> 适用场景：快速处理大量耗时较短的任务，如Netty的NIO接受请求时，可使用CachedThreadPool;
> corePoolSize = 0，maximumPoolSize = Integer.MAX_VALUE，即线程数量几乎无限制；
> keepAliveTime = 60s，线程空闲60s后自动结束。
> workQueue 为 SynchronousQueue 同步队列，这个队列类似于一个接力棒，入队出队必须同时传递，因为CachedThreadPool线程创建无限制，不会有队列等待，所以使用SynchronousQueue；
- Executors.newSingleThreadExecutor();
> corePoolSize与maximumPoolSize均为1，且不可修改，其他特征类似FixedThreadPool(1)
> 任务队列容量无限，任务按顺序执行
- Executors.newScheduledThreadPool(corePoolSize);
> 该线程池可以调度命令在给定延迟后运行或定期执行。
- Executors.newWorkStealingPool(); 
> 适用场景：耗时较长的计算任务（并行计算） 
> 返回一个 `ForkJoinPool` ，该`ForkJoinPool`的并行度为当前机器的cpu核心数




### ExecutorService

