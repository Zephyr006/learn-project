自旋锁（CAS）底层实现：`lock cmpxchg`，cmpxchg是汇编原语，不是原子性的，如果是单核新CPU，不需要lock；如果是多核心cpu，则必须使用lock来保证原子性

自旋锁的aba问题：使用版本号，标识锁竞争的对象是否被修改过

JOL观察对象的内存布局：ClassLayout.parseInstance(new Object()).toPrintable()

对象内存对齐：64位jvm，需要能被8整除

synchronized给对象上锁：修改堆内存中对象markword的值

对象中markword的构成：

偏向锁：当前线程指针被记录到

自旋锁什么时候升级为重量级：自旋次数超过10次，或者自旋线程数超过CPU核心数的一半  
=》 自适应自旋

自旋锁在等待时消耗CPU资源，但不需要额外的调度资源；重量级锁会在指定的队列中等待，等待时不消耗CPU资源，但是需要操作系统调度来完成作业的执行

synchronized锁升级过程：自旋锁 -> 偏向锁(有启动时延，默认jvm启动4s后可用) -> 重量级锁

Redis数据类型 - BitMap