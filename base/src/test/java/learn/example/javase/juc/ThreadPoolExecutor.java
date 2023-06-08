package learn.example.javase.juc;

import java.util.HashSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Zephyr
 * @since 2023-06-02
 */
public class ThreadPoolExecutor extends java.util.concurrent.ThreadPoolExecutor{
    private final AtomicInteger ctl = new AtomicInteger(ctlOf(RUNNING, 0));
    private static final int COUNT_BITS = Integer.SIZE - 3;
    private static final int CAPACITY   = (1 << COUNT_BITS) - 1;

    // runState is stored in the high-order bits
    private static final int RUNNING    = -1 << COUNT_BITS;
    private static final int SHUTDOWN   =  0 << COUNT_BITS;
    private static final int STOP       =  1 << COUNT_BITS;
    private static final int TIDYING    =  2 << COUNT_BITS;
    private static final int TERMINATED =  3 << COUNT_BITS;

    // Packing and unpacking ctl
    private static int runStateOf(int c)     { return c & ~CAPACITY; }
    private static int workerCountOf(int c)  { return c & CAPACITY; }
    private static int ctlOf(int rs, int wc) { return rs | wc; }


    private final BlockingQueue<Runnable> workQueue;

    private final ReentrantLock mainLock = new ReentrantLock();

    private final HashSet<Worker> workers = new HashSet<Worker>();

    private final Condition termination = mainLock.newCondition();

    private int largestPoolSize;

    private long completedTaskCount;

    private volatile ThreadFactory threadFactory;

    /**
     * Handler called when saturated or shutdown in execute.
     */
    private volatile RejectedExecutionHandler handler;

    private volatile long keepAliveTime;

    /**
     * If false (default), core threads stay alive even when idle.
     * If true, core threads use keepAliveTime to time out waiting
     * for work.
     */
    private volatile boolean allowCoreThreadTimeOut;

    /**
     * Core pool size is the minimum number of workers to keep alive
     * (and not allow to time out etc) unless allowCoreThreadTimeOut
     * is set, in which case the minimum is zero.
     */
    private volatile int corePoolSize;

    /**
     * Maximum pool size. Note that the actual maximum is internally
     * bounded by CAPACITY.
     */
    private volatile int maximumPoolSize;


    public ThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler, BlockingQueue<Runnable> workQueue1, int largestPoolSize, long completedTaskCount, ThreadFactory threadFactory1, RejectedExecutionHandler handler1, long keepAliveTime1, boolean allowCoreThreadTimeOut, int corePoolSize1, int maximumPoolSize1) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
        this.workQueue = workQueue1;
        this.largestPoolSize = largestPoolSize;
        this.completedTaskCount = completedTaskCount;
        this.threadFactory = threadFactory1;
        this.handler = handler1;
        this.keepAliveTime = keepAliveTime1;
        this.allowCoreThreadTimeOut = allowCoreThreadTimeOut;
        this.corePoolSize = corePoolSize1;
        this.maximumPoolSize = maximumPoolSize1;
    }


    private Runnable getTask() {
        boolean timedOut = false; // 上一次从任务队列中拉取任务是否超时? Did the last poll() time out?

        for (;;) {
            int c = ctl.get();
            int runState = runStateOf(c);

            // Check if queue empty only if necessary.
            if (runState >= SHUTDOWN && (runState >= STOP || workQueue.isEmpty())) {
                decrementWorkerCount();
                return null;
            }

            int workerCount = workerCountOf(c);

            // Are workers subject to culling?
            // 工作线程会被淘汰吗? -- 条件是 核心线程允许被淘汰,或者工作现场数大于核心线程数(则超过核心线程数的线程可以被淘汰)
            boolean timed = allowCoreThreadTimeOut || workerCount > corePoolSize;

            // ( 工作线程>最大线程数(兜底) or 允许淘汰线程并且已经超时 ) 且 ( 工作线程>1 or 没有要运行的任务 )
            // getTask方法第一次for循环的时候,一定不会因为超时而满足此处的if条件,因为timedOut变量为false
            if ((workerCount > maximumPoolSize || (timed && timedOut))
                && (workerCount > 1 || workQueue.isEmpty())) {
                // 用cas的方式减少线程数,如果扣减成功,则返回null, Worker线程收到null返回值后会结束自己的运行
                if (compareAndDecrementWorkerCount(c))
                    return null;
                // 如果线程数没有扣减成功,则继续进行for循环
                continue;
            }

            try {
                // 如果工作线程可以被淘汰,则阻塞等待指定时间,超时则返回null,使当前工作线程结束运行
                // 否则一直阻塞等待新任务的到来,不会受超时影响
                Runnable r = timed ?
                    workQueue.poll(keepAliveTime, TimeUnit.NANOSECONDS) :
                    workQueue.take();
                // 如果从任务队列中获取到了新任务,则返回给工作线程
                // 注意这里可能是在keepAliveTime时间内取到了值,也可能调用了take方法,等待时间超出了keepAliveTime
                if (r != null)
                    return r;
                // 走到这里,说明在keepAliveTime时间内没有取到任务,等待超时了,则记录状态值
                timedOut = true;
            } catch (InterruptedException retry) {
                timedOut = false;
            }
        }
    }

    public void execute(Runnable command) {
        if (command == null)
            throw new NullPointerException();
        /*
         * Proceed in 3 steps:
         *
         * 1. If fewer than corePoolSize threads are running, try to
         * start a new thread with the given command as its first
         * task.  The call to addWorker atomically checks runState and
         * workerCount, and so prevents false alarms that would add
         * threads when it shouldn't, by returning false.
         *
         * 2. If a task can be successfully queued, then we still need
         * to double-check whether we should have added a thread
         * (because existing ones died since last checking) or that
         * the pool shut down since entry into this method. So we
         * recheck state and if necessary roll back the enqueuing if
         * stopped, or start a new thread if there are none.
         *
         * 3. If we cannot queue task, then we try to add a new
         * thread.  If it fails, we know we are shut down or saturated
         * and so reject the task.
         */
        int c = ctl.get();
        // 如果运行的线程数少于corePoolSize,尝试用当前的command作为任务新增线程,addWorker方法会检查线程池运行状态确保合法,如果异常则返回false
        if (workerCountOf(c) < corePoolSize) {
            if (addWorker(command, true))
                return;
            c = ctl.get();
        }
        // 如果任务可以成功放入队列(容量没满)
        // 我们仍然需要仔细检查我们是否应该添加一个线程, 因为可能上次检查之后已存在的线程现在已经退出了,或者线程池现在已经shutdown了
        if (isRunning(c) && workQueue.offer(command)) {
            int recheck = ctl.get();
            // 如果线程池已经停止了,则尝试从队列中移除这个任务,并且拒绝这个任务
            if (! isRunning(recheck) && remove(command)) {
                // reject(command);
            // 或者说,现在已经没有正在运行的工作线程,则新创建一个工作线程(注意此处新创建的工作线程声明为非核心线程)
            }else if (workerCountOf(recheck) == 0)
                addWorker(null, false);
        }
        // 如果不能入队列,则尝试新增非核心线程,如果失败的话,那就是线程池shutdown了或者线程数也达到最大限制了,则使用拒绝策略拒绝执行此任务
        else if (!addWorker(command, false)) {
            // reject(command);
        }
    }

    private boolean addWorker(Runnable firstTask, boolean core) {
        retry:
        for (;;) {
            int c = ctl.get();
            int runState = runStateOf(c);

            // Check if queue empty only if necessary.
            if (runState >= SHUTDOWN &&
                !(runState == SHUTDOWN && firstTask == null && !workQueue.isEmpty()) )
                return false;

            for (;;) {
                int workerCount = workerCountOf(c);
                // 如果线程数超出指定容量限制,则直接返回false
                if (workerCount >= CAPACITY ||
                    workerCount >= (core ? corePoolSize : maximumPoolSize))
                    return false;
                // 如果能成功增加线程数
                if (compareAndIncrementWorkerCount(c))
                    break retry;
                c = ctl.get();  // Re-read ctl
                if (runStateOf(c) != runState)
                    continue retry;
                // else CAS failed due to workerCount change; retry inner loop
            }
        }

        boolean workerStarted = false;
        boolean workerAdded = false;
        ThreadPoolExecutor.Worker worker = null;
        try {
            worker = new ThreadPoolExecutor.Worker(firstTask);
            final Thread thread = worker.thread;
            if (thread != null) {
                final ReentrantLock mainLock = this.mainLock;
                mainLock.lock();
                try {
                    // Recheck while holding lock.
                    // Back out on ThreadFactory failure or if
                    // shut down before lock acquired.

                    // 在持有锁的情况下重新检查线程池状态。在 ThreadFactory 失败或在获取锁之前关闭时退出
                    int rs = runStateOf(ctl.get());

                    if (rs < SHUTDOWN ||
                        (rs == SHUTDOWN && firstTask == null)) {
                        if (thread.isAlive()) // precheck that thread is startable
                            throw new IllegalThreadStateException();
                        workers.add(worker);
                        int s = workers.size();
                        if (s > largestPoolSize)
                            largestPoolSize = s;
                        workerAdded = true;
                    }
                } finally {
                    mainLock.unlock();
                }
                if (workerAdded) {
                    thread.start();
                    workerStarted = true;
                }
            }
        } finally {
            if (! workerStarted) {
                // addWorkerFailed(worker);
            }
        }
        return workerStarted;
    }

    private void decrementWorkerCount() {
        do {} while (! compareAndDecrementWorkerCount(ctl.get()));
    }

    private boolean compareAndDecrementWorkerCount(int expect) {
        return ctl.compareAndSet(expect, expect - 1);
    }

    private boolean compareAndIncrementWorkerCount(int expect) {
        return ctl.compareAndSet(expect, expect + 1);
    }

    private static boolean isRunning(int c) {
        return c < SHUTDOWN;
    }

    private static boolean runStateAtLeast(int c, int s) {
        return c >= s;
    }

    final void runWorker(ThreadPoolExecutor.Worker w) {
        Thread wt = Thread.currentThread();
        Runnable task = w.firstTask;
        w.firstTask = null;
        w.unlock(); // allow interrupts
        boolean completedAbruptly = true;
        try {
            // 如果有可供运行的任务,
            while (task != null || (task = getTask()) != null) {
                // Worker对象加aqs锁
                w.lock();
                // If pool is stopping, ensure thread is interrupted;
                // if not, ensure thread is not interrupted.  This
                // requires a recheck in second case to deal with
                // shutdownNow race while clearing interrupt
                // 如果线程池正在停止,确保线程已经被中断;
                // 如果没有,确保线程未中断。这需要在第二种情况下重新检查，以便在清除中断时处理Shutdown Now竞争
                if ((runStateAtLeast(ctl.get(), STOP) ||
                    (Thread.interrupted() &&
                        runStateAtLeast(ctl.get(), STOP))) && !wt.isInterrupted())
                    wt.interrupt();
                try {
                    beforeExecute(wt, task);
                    Throwable thrown = null;
                    // 运行用户的任务,并catch异常,使用临时变量保存,这个异常可以在 afterExecute() 方法中被处理
                    try {
                        task.run();
                    } catch (RuntimeException x) {
                        thrown = x; throw x;
                    } catch (Error x) {
                        thrown = x; throw x;
                    } catch (Throwable x) {
                        thrown = x; throw new Error(x);
                    } finally {
                        // afterExecute默认是空的方法实现,说明用户任务出现异常时,用户是感知不到的,需要用户在run方法中自己捕捉异常并处理
                        afterExecute(task, thrown);
                    }
                } finally {
                    // 用户任务运行完成,完成任务数+1,且Worker解锁
                    task = null;
                    w.completedTasks++;
                    w.unlock();
                }
            }
            // 如果能正常的运行到这里,必然不是被中断的
            completedAbruptly = false;
        } finally {
            // 如果没有可供运行的任务,则Worker退出 - completedAbruptly 是否由于用户异常导致的线程退出
            //? processWorkerExit(w, completedAbruptly);
        }
    }

    private final class Worker
        extends AbstractQueuedSynchronizer
        implements Runnable
    {
        /**
         * This class will never be serialized, but we provide a
         * serialVersionUID to suppress a javac warning.
         */
        private static final long serialVersionUID = 6138294804551838833L;

        /** Thread this worker is running in.  Null if factory fails. */
        final Thread thread;
        /** Initial task to run.  Possibly null. */
        Runnable firstTask;
        /** Per-thread task counter */
        volatile long completedTasks;

        /**
         * Creates with given first task and thread from ThreadFactory.
         * @param firstTask the first task (null if none)
         */
        Worker(Runnable firstTask) {
            setState(-1); // inhibit interrupts until runWorker
            this.firstTask = firstTask;
            this.thread = getThreadFactory().newThread(this);
        }

        /** Delegates main run loop to outer runWorker  */
        public void run() {
            runWorker(this);
        }

        // Lock methods
        //
        // The value 0 represents the unlocked state.
        // The value 1 represents the locked state.

        protected boolean isHeldExclusively() {
            return getState() != 0;
        }

        protected boolean tryAcquire(int unused) {
            if (compareAndSetState(0, 1)) {
                setExclusiveOwnerThread(Thread.currentThread());
                return true;
            }
            return false;
        }

        protected boolean tryRelease(int unused) {
            setExclusiveOwnerThread(null);
            setState(0);
            return true;
        }

        public void lock()        { acquire(1); }
        public boolean tryLock()  { return tryAcquire(1); }
        public void unlock()      { release(1); }
        public boolean isLocked() { return isHeldExclusively(); }

        void interruptIfStarted() {
            Thread t;
            if (getState() >= 0 && (t = thread) != null && !t.isInterrupted()) {
                try {
                    t.interrupt();
                } catch (SecurityException ignore) {
                }
            }
        }
    }
}
