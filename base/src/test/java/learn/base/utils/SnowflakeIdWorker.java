package learn.base.utils;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * 雪花算法的实现
 *
 * 1. 最高位是符号位，始终为0，不可用。
 * 2. 41位的时间序列，精确到毫秒级，41位的长度可以使用69年。时间位还有一个很重要的作用是可以根据时间进行排序。
 * 3. 10位的机器标识，10位的长度最多支持部署1024个节点。
 * 4. 12位的计数序列号，序列号即一系列的自增id，可以支持同一节点同一毫秒生成多个ID序号，12位的计数序列号支持每个节点每毫秒最多产生4096个ID序号。
 *
 * @author Zephyr
 * @since 2022-1/9.
 */
public class SnowflakeIdWorker {
    private static final int BITS_OF_TIMESTAMP = 41;  // 能保存69年的
    private static final int BITS_OF_MACHINE = 10;    // 最多1024个机器节点
    private static final long BITS_OF_SEQUENCE = 12;  // 最大4096个序列号
    /** 机器ID向左移12位 */
    private final long workerIdShift = BITS_OF_SEQUENCE;
    /** 时间截向左移22位(10+12) */
    private static final long timestampLeftShift = BITS_OF_SEQUENCE + BITS_OF_MACHINE;
    /** 最大序列号 */
    private static final long MAX_SEQUENCE = (1 << BITS_OF_SEQUENCE) - 1;
    /** 起始时间戳 */
    private static final long START_TIMESTAMP = LocalDateTime.of(2020, 1, 1, 0, 0, 0)
            .toInstant(ZoneOffset.of("+08")).toEpochMilli();    // 上次生成
    private final long dataCenterId;    // 数据中心id
    private final long machineId;       // 机器id
    private static volatile long lastStamp = 0L;    // 上次生成id的时间戳
    private static volatile long seq = 0L; // 相同时间单位内的自增序列号，从0开始

    public SnowflakeIdWorker(long dataCenterId, long machineId) {
        this.dataCenterId = dataCenterId;
        this.machineId = machineId;
    }

    public synchronized long nextId() {
        long currTime = getNewTimestamp();

        // 当前时间戳小于上次的时间戳，说明发生了时钟回拨,   抛出异常或者等待时间追上之前的时间
        if (currTime < lastStamp) {
            throw new RuntimeException("Clock moved backwards, refuse to generate id.");
            //try {
            //    Thread.sleep(lastStamp - currTime);
            //} catch (InterruptedException e) {
            //    e.printStackTrace();
            //}
            //return nextId();
        }

        // 当前时间戳与上次生成id的时间戳在同一毫秒内，则自增序列号
        if (currTime == lastStamp) {
            seq = (seq + 1) & MAX_SEQUENCE;
            // 同一时间戳内的序列号已经达到最大，则获取下一时间戳并赋值给现在的时间戳
            if (seq == 0L) {
                currTime = getNextTimeStamp();
            }
        } else {
            seq = 0L;
        }
        lastStamp = currTime;
        return (lastStamp - START_TIMESTAMP) << timestampLeftShift  // 时间戳部分
                | dataCenterId << workerIdShift         // 数据中心部分
                | machineId << 5                       // 机器部分
                | seq;                                  // 序列号部分
    }

    private static long getNextTimeStamp() {
        long newTimestamp = getNewTimestamp();
        while (newTimestamp <= lastStamp) {
            newTimestamp = getNewTimestamp();
        }
        return newTimestamp;
    }

    private static long getNewTimestamp() {
        return System.currentTimeMillis();
    }

    public static void main(String[] args) {
        //SnowFlake snowFlake = new SnowFlake(2, 3);
        //
        //for (int i = 0; i < (1 << 4); i++) {
        //    //10进制
        //    System.out.println(snowFlake.nextId());
        //}
        System.out.println(2 & MAX_SEQUENCE);
    }

}
