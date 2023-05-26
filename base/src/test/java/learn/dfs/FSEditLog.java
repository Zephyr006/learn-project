package learn.dfs;

import java.util.LinkedList;

/**
 * 负责管理edit log日志
 *
 * @author Zephyr
 * @since 2021-12-19.
 */
public class FSEditLog {

    private long txid = 0L;
    private DoubleBuffer editLogBuffer = new DoubleBuffer();

    private volatile Boolean isSyncRunning = false;
    private volatile Boolean isWaitSync = false;
    private volatile Long syncMaxTxid = 0L;


    // 每个线程都会有自己的一个副本
    private ThreadLocal<Long> localTxid = new ThreadLocal<>();

    /**
     * 写日志，这里肯定是一个高并发的请求
     */
    public void logEdit(String content){
        /**
         *  假设过来了三个线程
         */
        synchronized(this){
            // 创建一个日志编号
            txid ++;
            // 线程1的第一条日志的编号就是1
            localTxid.set(txid);
            // 创建一个日志对象
            EditLog log = new EditLog(txid,content);
            // 将数据写入当前内存 editLogBuffer是双缓冲对象
            editLogBuffer.write(log);
        }
        // 释放锁， 其他线程上锁，开始循环

        logSync();
    }

    private void logSync(){
        // 加锁
        synchronized(this){
            // 判断 SyncRunning 是否在刷写磁盘
            if(isSyncRunning){
                // 如果现在正在刷写磁盘，先查看当前线程的日志的编号
                long txid = localTxid.get();
                // 如果当前日志编号小于正在刷写磁盘的最大日志编号的话，退出
                if(txid <= syncMaxTxid){
                    return;
                }
                // 判断是否在等待刷写磁盘  加入已经有线程在等待刷写，其他线程就不必在等待
                if(isWaitSync){
                    return;
                }
                // 修改isWaitSync 状态
                isWaitSync = true;
                // 判断是否有人在刷写磁盘
                while(isSyncRunning){
                    try{
                        // 释放锁
                        // 1)被唤醒
                        // 2)到时间
                        wait(2000);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                isWaitSync = false;
            }

            // 没有刷写磁盘的话，直接交换内存  （内存里面数据达到阈值，交换内存）
            editLogBuffer.setReadyToSync();

            // 内存交换
            if(editLogBuffer.currentBuffer.size() > 0){
                syncMaxTxid = editLogBuffer.getSyncMaxTxid();
            }

            // 修改状态
            isSyncRunning = true;
        }

        // 内存中的数据写入磁盘  写磁盘操作没有加锁  分段加锁
        editLogBuffer.flush();

        synchronized(this){
            // 修改状态
            isSyncRunning = false;
            // 唤醒wait
            notify();
        }

    }
}

/**
 * 使用了面向对象的思想，把一条日志看成一个对象
 */
class EditLog{
    // 日志编号
    long txid;
    // 日志内容
    String content;

    // 构造函数
    public EditLog(long txid,String content){
        this.txid = txid;
        this.content = content;
    }

    @Override
    public String toString(){
        return "EditLog{"+
                "txid="+txid+
                ",content='"+content+"\'"+
                "}";
    }
}

/**
 * 双缓冲方案
 */
class DoubleBuffer{
    // 内存1
    LinkedList<EditLog> currentBuffer = new LinkedList<>();
    // 内存2
    LinkedList<EditLog> syncBuffer = new LinkedList<>();

    // 向内存1中写入日志
    public void write(EditLog log){
        currentBuffer.add(log);
    }

    // 内存1和内存2交换
    public void setReadyToSync(){
        LinkedList<EditLog> tmp = currentBuffer;
        currentBuffer = syncBuffer;
        syncBuffer = tmp;
    }

    // 获取当前刷磁盘的内存里的ID最大值
    public long getSyncMaxTxid(){
        return syncBuffer.getLast().txid;
    }

    public void flush(){
        for(EditLog log:syncBuffer){
            System.out.println("存入磁盘日志信息"+log);
        }
        syncBuffer.clear();
    }
}
