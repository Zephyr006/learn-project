# JUC包下的重点接口/类信息汇总

### Lock
- ReentrantLock          可重入锁
- ReentrantReadWriteLock 读写锁（偏悲观）
- StampedLock            读写锁（偏乐观）

### ThreadPool
- ExecutorService       线程池管理的常用接口
- ThreadPoolExecutor    线程池最常用的核心实现
- ForkJoinPool          “分而治之(Fork/Join)”思想的运行`ForkJoinTask`任务的线程池实现 
- CountedCompleter      `ForkJoinPool`的子类，主要处理和数量相关的业务
- Executors             工厂类：快捷创建JUC包下的一些类

### Map
- ConcurrentHashMap     最常用的线程安全Map，存取速度是`ConcurrentSkipListMap` 的4倍左右。
- ConcurrentSkipListMap 基于跳表实现的Map，key有序，比`ConcurrentHashMap`支持更高的并发

### Queue(单向队列) / Deque(双向队列)
使用阻塞算法的队列可以用一个锁（入队和出队用同一把锁）或两个锁（入队和出队用不同的锁）等方式来实现，
而非阻塞的实现方式则可以使用循环CAS的方式来实现
- ArrayBlockingQueue     基于数组的阻塞队列，使用一把并发锁实现
- PriorityBlockingQueue  一个支持优先级的无界阻塞队列
- ConcurrentLinkedQueue / ConcurrentLinkedDeque 线程安全的非阻塞队列，使用自旋锁（CAS）实现
- LinkedBlockingQueue / LinkedBlockingDeque     线程安全的阻塞队列，使用并发锁实现
- SynchronousQueue       一个内部最多只能包含**1个**元素的阻塞队列
- LinkedTransferQueue    无界阻塞队列，额外提供了预占模式，当有消费者在等待时，生产者线程不进入队列，直接被消费者线程消费
- DelayQueue             一个支持延时获取元素的无界阻塞队列。内部用 `PriorityQueue` 实现

### List
- CopyOnWriteArrayList  写入时复制的线程安全List

### Set
- ConcurrentSkipListSet 基于跳表的`NavigableSet`实现，元素有序
- CopyOnWriteArraySet   写入时复制的Set实现，保存的元素无序

### Others
- CountDownLatch    通过一个计数器，使一个线程等待其他线程都执行完毕后再继续执行（count==0）
- CyclicBarrier     一组线程相互等待，待所有线程执行完成，运行`barrierCommand`，参照`CountDownLatch`
- Semaphore         限制对指定资源的访问线程数
- CompletableFuture 
- ThreadLocalRandom 依赖操作系统底层的安全型随机生成器
- Phaser            控制多个线程分阶段共同完成任务
- RecursiveAction   ForkJoinTask的子类，无返回值
- RecursiveTask<V>  ForkJoinTask的子类，返回泛型值
