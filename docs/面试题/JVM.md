## JVM内存调优监控工具

[https://blog.csdn.net/luzhensmart/article/details/105848424](https://blog.csdn.net/luzhensmart/article/details/105848424)

- jps：列出程序中有哪些Java进程；
- jinfo：列出和JVM相关的属性；
- jstack：列出当前Java进程中有哪些线程。
- jmap -histo pid | head -20：
- jconsole、jvisualvm：JVM内存查看
- jad：反编译出正在运行的程序的源码

## G1并发标记算法的核心：三色标记

- 白色：未被标记的对象
- 灰色：自身被标记，成员变量未被标记
- 黑色：自身和成员变量均已标记完成

## 并发标记算法常见问题

![image-20200712213214845](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20200712213214845.png)

![image-20200712213310730](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20200712213310730.png)