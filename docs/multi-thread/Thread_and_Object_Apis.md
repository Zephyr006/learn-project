### 多线程相关常用API
Thread类api：
```
public class Thread() implements Runnable {

/**
 * 使当前正在执行的线程休眠（暂停执行）指定的毫秒数，实际效果取决于系统计时器和调度程序的精度和准确性。
 *  该线程不丢失任何监视器的所属权。
 * PARAMS：
 * millis - 睡眠的时间长度，以毫秒为单位
 * throws：
 * IllegalArgumentException -如果值millis为负
 * InterruptedException -如果任何线程中断了当前线程。 当这个异常被抛出时，当前线程的中断状态被清除。
 */
public static native void sleep(long millis) throws InterruptedException;

最多等待millis毫秒该线程终止。 millis==0意味着永远等待。
此实现是在this.isAlive==true的条件下循环调用this.wait方法。线程终止是通过调用this.notifyAll方法。 建议应用不要在Thread实例上使用wait， notify，或notifyAll。
PARAMS：
millis - 要等待的时间（单位：毫秒）
抛出：
IllegalArgumentException -如果值millis为负
InterruptedException -如果任何线程中断了当前线程。 当这个异常被抛出时，当前线程的中断状态会被清除。
public final synchronized void join(long millis) throws InterruptedException;

//
public static native void yield();

/**
 * 打断当前线程（当前线程自己打断自己不行），同时调用checkAccess方法，checkAccess方法可能会抛出SecurityException异常
 * 如果这个线程被Object类的wait方法的请求所阻塞，或者是join方法、sleep方法（即：这个线程正处在等待或睡眠状态，并没有处在运行态）
 * 那么interrupt状态将会被清除，并收到InterruptedException异常
 * 如果此线程在InterruptibleChannel上的I/O操作被阻止，则channel将被关闭，线程被设置为interrupt状态，并且线程会收到ClosedByInterruptException异常
 * 如果此线程被阻塞在java.nio.channels.Selector，则线程将被置为interrupt状态，并立刻从selection操作中返回，返回值可能是非零值（好像是selector的wakeup方法被调用了一样）
 * 如果没有上述情况发生，则这个线程将被置为interrupt状态
 * 打断（interrupt）一个未活跃（alive）的线程不会产生任何效果
 * throws：
 * SecurityException -如果当前线程不能修改该线程
 */
public void interrupt()

/**
 * 返回该线程的状态。 此方法被设计以用于监测的系统状态，而不是同步控制。
 */
public State getState()
}
```



Object类的api：
```
public class Object {

/**
 * 使当前的线程等待直到其他线程调用本对象的notify()方法或notifyAll()方法，或者超过timeout时间（超时）。
 * 当前线程必须拥有该对象的监视器（monitor，如synchronized(this)中的this）！直到其他线程调用了notify/notifyAll方法，或等待时间超过timeout时间，当前线程才释放对应的监视器（monitor）
 * 然后，当前线程继续等待，直到重新获得监视器的所有权，才继续执行
 * wait方法应该只会被当前对象的监视器持有者调用。
 * 线程还可以唤醒不被通知，中断，或超时，所谓虚假唤醒 。 虽然这会在实际中很少出现，
 * 应用程序必须通过测试为已经导致线程被唤醒的条件，并继续等待，如果条件不成立防范它。 换句话说，等待应总是发生在循环中，像这样的：
 *        synchronized (obj) {
 *            while (<condition does not hold>)
 *                obj.wait(timeout);
 *            ... // Perform action appropriate to condition
 *        }
 * 使用方法方面：wait() === wait(0) === wait(0,0) 
 *
 * 如果当前线程没有抛出InterruptedException异常，当前对象会等待，直到如上所述的该对象的锁定状态已经恢复。
 * 请注意， wait的方法，因为它当前线程放入等待此对象设置，解锁仅此对象; 而线程等待在其上当前线程可以被同步的任何其他对象保持锁定。
 * 此方法只能由一个线程，它是此对象监视器的所有者被调用。
 *
 * PARAMS：
 * timeout - 以毫秒为单位，以等待的最长时间。
 * throws：
 * IllegalArgumentException -如果超时值为负。
 * IllegalMonitorStateException -如果当前线程不是这个对象监视器的所有者。
 * InterruptedException -如果任何线程在当前线程收到通知（notification）之前或在等待时，打断了当前线程，
 *                         会抛出InterruptedException。 当这个异常被抛出后当前线程的中断状态被清除。
 */
public native void wait(long timeout) throws InterruptedException;

/**
 * 唤醒在此对象监视器上等待的单个线程。 如果任何线程此对象上等待，它们中的其中一个将被唤醒。 选择是任意的，并由具体实现来裁定。
 *  线程通过调用的一个对象的监视器上等待wait的方法。
 * 唤醒的线程将无法继续进行，直到当前线程放弃此对象的锁。 被唤醒的线程将以通常的方式与可能正在主动竞争以与此对象进行同步的任何其他线程竞争； 例如，被唤醒的线程在成为锁定该对象的下一个线程时没有任何可靠的特权或劣势（即：与其他线程公平竞争）。
 * 此方法只应该被拥有此对象监视器的线程调用。线程成为对象监视器的所有者有三种方法：
 * - 通过执行此对象的同步实例方法。
 * - 通过执行synchronized语句内的代码主题，该synchronized语句锁定的是当前对象（形如：synchronized(this)）。
 * - 通过执行体synchronized的对象上进行同步的语句。
 * - 对于类型为Class的对象,通过执行该类的同步静态方法。
 * 一次只能有一个线程拥有对象的监视器。
 * throws：
 * IllegalMonitorStateException -如果当前线程不是这个对象监视器的所有者。
 */
public native void notify();

/**
 * 唤醒‘等待该对象的监视器’的全部线程。 线程通过调用wait方法等待对象的监视器。
 * notifyAll方法与notify方法基本一致，不同之处在于notifyAll唤醒等待该对象监视器的所有线程，而notify只会唤醒等待线程中的一个
 * throws：
 * IllegalMonitorStateException -如果当前线程不是这个对象监视器的所有者。
 */
public native void notifyAll()

}
```