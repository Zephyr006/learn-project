## ConcurrentHashMap

**更详细的解释**：[http://www.importnew.com/28263.html](http://www.importnew.com/28263.html)

###### Java8：

> Java8中的HashMap和ConcurrentHashMap 由数组+链表+红黑树 组成。
>
> 根据 Java7 HashMap 的介绍，我们知道，查找的时候，根据 hash 值我们能够快速定位到数组的具体下标，但是之后的话，需要顺着链表一个个比较下去才能找到我们需要的，时间复杂度取决于链表的长度，为 O(n)。 为了降低这部分的开销，在 Java8 中，**当链表中的元素超过了 8 个以后，会将链表转换为红黑树**，在这些位置进行查找的时候可以降低时间复杂度为 O(logN)。
>
> Java7 中使用 Entry 来代表每个 HashMap 中的数据节点，Java8 中使用 Node，基本没有区别，都是 key，value，hash 和 next 这四个属性，不过，Node 只能用于链表的情况，红黑树的情况需要使用 TreeNode。我们根据数组元素中，第一个节点数据类型是 Node 还是 TreeNode 来判断该位置下是链表还是红黑树的。

###### **Java7**

> 整个 ConcurrentHashMap 由一个个 Segment 组成，可以理解为ConcurrentHashMap 是一个 Segment 数组，Segment 通过继承 ReentrantLock 来进行加锁，所以每次需要加锁的操作锁住的是一个 segment，这样只要保证每个 Segment 是线程安全的，也就实现了全局的线程安全。ConcurrentHashMap 在执行get操作时不会加锁，添加节点的操作 put 和删除节点的操作 remove 都是要加 segment 上的独占锁。
>
> - concurrencyLevel：并行级别、并发数、Segment 数。默认是 16，也就是说 ConcurrentHashMap 有 16 个 Segments，所以理论上，这个时候，最多可以同时支持 16 个线程并发写，只要它们的操作分别分布在不同的 Segment 上。**这个值可以在初始化的时候设置为其他值，但是一旦初始化以后，它是不可以扩容的**。
>
> - initialCapacity：初始容量，这个值指的是整个 ConcurrentHashMap 的初始容量，实际操作的时候需要平均分给每个 Segment。
>
> - loadFactor：负载因子，之前我们说了，Segment 数组不可以扩容，所以这个负载因子是给每个 Segment 内部使用的（每个Segment 的默认大小为 2，插入第二个元素时会进行第一次扩容）。

## Java中类加载/实例化顺序的问题

- 如果调用的是子类的普通方法，JVM实例化的内容及顺序如下：

> 父类的静态成员变量~
> 父类的静态代码块~
> 子类的静态成员变量~
> 子类的静态代码块~
> 子类的方法~

- 如果调用的是子类的静态方法，JVM实例化的内容及顺序如下：

> 父类的静态成员变量~
> 父类的静态代码块~
> 子类的静态成员变量~
> 子类的静态代码块~
> 父类的构造方法~
> 子类的普通成员变量~
> 子类的构造方法~
> 子类的方法~

- 如果调用的是子类的静态成员变量，JVM实例化的内容及顺序如下（与调用父类的静态成员变量结果相同）：

> 父类的静态成员变量~
> 父类的静态代码块~
> 子类的静态成员变量~
> 子类的静态代码块~

## 单例模式

```java
// 饿汉式：线程安全
public class Singleton {
    private static Singleton instance = new Singleton();
    private Singleton() { }

    public static Singleton getInstance() {
        return instance;
    }
}
```

```java
// 懒汉式：双重校验锁法（用volatile关键字确保线程安全）
public class Singleton {
    private volatile static Singleton instance;
    private Singleton() { }

    public static Singleton getInstance() {
        if (null == instance) {            //双重检查
            synchronized (Singleton.class) {
                if (null == instance) {
                    instance = new Singleton();
                }
            }
        }
        return instance;
    }
}
```

## 快速失败和安全失败的区别





## 对于String s = new String(“abc”)的处理过程