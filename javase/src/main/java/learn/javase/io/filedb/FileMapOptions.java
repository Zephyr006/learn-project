package learn.javase.io.filedb;

/**
 * @author Zephyr
 * @date 2020/5/27.
 */
public class FileMapOptions {
    /**
     * FileChannel 的文件读取模式  对应常量 {MODE_*}
     * RandomAccessFile打开文件支持的 4种模式：
     * “r” 以只读方式打开。调用结果对象的任何 write 方法都将导致抛出 IOException。
     * “rw” 打开以便读取和写入。如果该文件尚不存在，则尝试创建该文件。
     * “rws” 打开以便读取和写入，对于 “rw”，还要求对文件的内容或元数据的每个更新都同步写入到底层存储设备。
     * “rwd” 打开以便读取和写入，对于 “rw”，还要求对文件内容的每个更新都同步写入到底层存储设备。
     * 其中rws模式会在open文件时传入O_SYNC标志位。rwd模式会在open文件时传入O_DSYNC标志位。
     */
    private String fileMode = FILE_MODE_RW;
    public static final String FILE_MODE_R = "r";
    public static final String FILE_MODE_RW = "rw";
    public static final String FILE_MODE_RWS = "rws";
    public static final String FILE_MODE_RWD = "rwd";

    /**
     * 刷盘策略：何时执行数据写入操作
     * SYNC=>每次写入/更新数据都实时刷盘，数据不丢失，但会影响性能
     * ASYNC=>达到一定数量后批量将数据写入磁盘，部分数据可能会丢失，但性能更强
     */
    private String syncMode = SYNC;
    public static final String SYNC = "sync";
    public static final String ASYNC = "async";

    /**
     *  当刷盘策略为 ASYNC 时，每写入 maxAsyncCount 个数据时执行一次落盘/数据保存操作
     *  建议值：5 ~ 20   大于100性能几乎不再有提升
    */
    private int maxAsyncCount = 2022;

    /**
     * 一个DB文件能保存的最大字节数，达到该值，DB文件将被切分，只能设置为小于此初始值！！
     */
    public int MAX_DB_SIZE = 9999_7999;



    public static FileMapOptions async() {
        return new FileMapOptions().setSyncMode(ASYNC);
    }

    public String getFileMode() {
        return fileMode;
    }

    public FileMapOptions setFileMode(String fileMode) {
        this.fileMode = fileMode;
        return this;
    }

    public String getSyncMode() {
        return syncMode;
    }

    public FileMapOptions setSyncMode(String syncMode) {
        this.syncMode = syncMode;
        return this;
    }

    public int getMaxAsyncCount() {
        return maxAsyncCount;
    }

    public FileMapOptions setMaxAsyncCount(int maxAsyncCount) {
        this.maxAsyncCount = maxAsyncCount;
        return this;
    }


}
