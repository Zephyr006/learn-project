## MySQL如何实现数据主从同步

MySQL 默认采用异步复制方式（主节点不会主动push bin log到从节点），这样从节点不用一直访问主服务器来更新自己的数据。当slave节点连接master时，会主动从master处获取最新的bin log文件。

**原理**：

![img](https://pic1.zhimg.com/80/v2-1b0c3f31bd398c39b9e0930059b0ca24_720w.jpg)

1. Master节点将数据的改变记录到二进制`Binlog`日志；
2. 当一个从节点连接主节点时，主节点会创建一个log dump 线程，用于发送bin-log的内容。在读取bin-log中的操作时，此线程会对主节点上的bin-log加锁，当读取完成，甚至在发送给从节点之前，锁会被释放。

3. Slave服务器会在一定时间间隔内对Master二进制日志进行探测其是否发生改变。如果发生改变，从节点会启动<u>两个线程</u>来同步Master的二进制事件（执行完一轮同步操作后，I/OThread和SQLThread将进入睡眠状态，等待下一次被唤醒）：

   - I/O线程：连接主节点，请求主库中的bin-log。I/O线程接收到主节点binlog dump 线程发来的binlog更新之后，保存在本地relay-log中。

   - SQL 线程：会读取 relay log 文件中的日志，并解析成具体的操作，来实现主从的数据一致。

**主从同步的必要条件**：

- 主库开启binlog日志（设置log-bin参数）
- 主从server-id不同
- 从库服务器能连通主库


## MySQL大数据量查询优化

- 用 exists 代替 in 、能用 between 就不要用 in
- 用具体的字段列表代替“*”，不要返回用不到的任何字段
- 在 where 及 order by 涉及的列上建立索引，如：唯一索引（UNIQUE）、普通索引（INDEX）等
- 一个表的索引数最好不要超过6个
- 尽量使用多表连接（join）查询来代替子查询
- 避免向客户端返回大数据量
- 避免在where子句中使用前模糊匹配(可以使用后模糊匹配)，如：select id from t where name like ‘%李%’。若要提高效率，可以考虑全文索引（`FULLTEXT INDEX`）
- 在使用索引字段作为条件时，如果该索引是复合索引，那么必须使用到该索引中的第一个字段作为条件时才能保证系统使用该索引！并且应尽可能的让字段顺序与索引顺序相一致。
- 当索引列有大量数据重复时，SQL查询可能不会去利用索引，如一表中有字段sex，male、female几乎各一半，那么即使在sex上建了索引也对查询效率起不了作用
- 避免使用游标，因为游标的效率较差，如果游标操作的数据超过1万行，就应该考虑改写

- 以下操作应尽量避免，否则引擎将放弃使用索引而进行全表扫描
  - 在 where 子句中**对字段进行 null 值判断**（应设置字段默认值）
  - 在 where 子句中使用 **or** 来连接条件
  - 在 where 子句中使用 **!= 和 <>** 操作符
  - 在 where 子句中使用 **in 和 not in**
  - 在 where 子句中对字段进行**函数或表达式操作**
  - 在 where 子句中的 **“=”左边使用参数、函数或算术运算**

- 设置Redis缓存、分库分表、读写分离

## MySQL大数据量分页查询优化
- 使用`JOIN`优化：`select * from t_test a INNER JOIN (select id from t_test LIMIT 123456,50) b on a.id=b.id;`
- 在查询记录量低于100时，查询时间基本没有差距，随着查询记录量越来越大，所花费的时间也会逐渐增多。
- 随着查询偏移的增大，尤其查询偏移大于1万以后，查询时间急剧增加。

## MySQL存储引擎的区别
> InnoDB：支持事务安全的引擎，**支持外键、行锁、事务**。如果有大量的update和insert，建议使用InnoDB，特别是针对多个并发和QPS较高的情况。  
> MyISAM：ISAM是Indexed Sequential Access Method (有索引的顺序访问方法) 的缩写，它是存储记录和文件的标准方法。**不支持事务和外键**，如果执行大量的select，insert MyISAM比较适合。  
  
区别：
- InnoDB是聚集索引，使用B+Tree作为索引结构，数据文件是和（主键）索引绑在一起的（表数据文件本身就是按B+Tree组织的一个索引结构），表必须要有主键，通过主键索引效率很高。
  MyISAM是非聚集索引，也是使用B+Tree作为索引结构，索引和数据文件是分离的，索引保存的是数据文件的指针。主键索引和辅助索引是独立的。
- InnoDB不保存表的具体行数，执行`select count(*) from table`时需要全表扫描。而MyISAM用一个变量保存了整个表的行数，执行上述语句时只需要读出该变量即可，速度很快。
- InnoDB支持表、行级锁(默认)，而MyISAM支持表级锁。
- InnoDB表必须有主键（用户没有指定的话会自己找或生产一个主键），而MyISAM可以没有。



## MySQL的 B+ Tree 索引

## [MySQL Explain关键字详解](https://www.cnblogs.com/tufujie/p/9413852.html)






