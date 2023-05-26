package learn.example.javase.io.filedb;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import learn.example.javase.io.filedb.util.FileChannelUtils;
import learn.example.javase.io.filedb.util.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.StampedLock;

/**
 * todo 指定大小的文件切割 - MAX_DB_SIZE
 * todo 按照日期切割文件
 * （finished） 增加异步刷盘的支持，异步刷盘需要写入达到一定数量后执行强制刷盘
 * （Deprecated） 读写文件时申请操作系统中的内存，并回收
 * @author Zephyr
 * @since 2020-05-25.
 */
public class FileMapImpl<V extends Serializable> extends AbstractFileMap<V> {
    private static final Logger logger = LoggerFactory.getLogger(FileMapImpl.class);

    private final StampedLock lock = new StampedLock();
    private Integer unwrittenCounter = 0;


    public FileMapImpl(String dbName, FileMapOptions op) {
        options = op;
        this.dbName = dbName;
        try {
            File dbDir = new File(dir);
            if (!dbDir.exists()) {
                FileUtils.forceMkdir(dbDir);
            }
            RandomAccessFile dbFile = new RandomAccessFile(dir + dbName + dbSuffix, options.getFileMode());
            RandomAccessFile indexFile = new RandomAccessFile(dir + dbName + indexSuffix, options.getFileMode());
            dbChannel = dbFile.getChannel();
            indexChannel = indexFile.getChannel();
            dbChannel.position(dbChannel.size());
            indexChannel.position(indexChannel.size());
        } catch (IOException e) {
            logger.error("FileMap init failed!!!", e);
        }
    }

    @Override
    public V get(String key) {
        if (!isOpen())  return null;
        waitForInit();

        Integer hashCode = key.hashCode();
        MutablePair<FileMapIndex, V> pair = getCache(hashCode);
        return Objects.isNull(pair) ? null : pair.right;
    }

    private V put(String key, V value, String syncMode) throws JsonProcessingException {
        waitForInit();
        // 把要保存的value转换为最终保存的格式
        FileMapSegment<V> segment = new FileMapSegment<>(key, value);
        int hashCode = key.hashCode();

        FileMapIndex index;
        FileLock dbLock = null;
        FileLock indexLock = null;
        long stamped = 0;
        try {
            //byte[] valueBytes = objMapper.writeValueAsBytes(segment);
            byte[] valueBytes = JSON.toJSONBytes(segment);
            MutablePair<FileMapIndex, V> oldCache = getCache(hashCode);
            stamped = lock.writeLock();
            // 下面这条语句不加锁访问时会导致数据出错，不信试试
            long dbOriginPosition = dbChannel.position();
            if (oldCache != null) {
                // 如果新put的key和value都与已存在的缓存值相同，则直接返回value，不再向下执行
                if (value.equals(oldCache.right) && key.hashCode()==oldCache.left.hash) {
                    return value;
                } else {
                // 已存在相同的key，但value值不同，执行update
                    index = new FileMapIndex(oldCache.left.indexPosition, hashCode,
                                            dbOriginPosition, valueBytes.length);
                }
            } else {
                // insert或update索引信息都是要向索引文件中追加信息，不同之处在于update操作要处理原有缓存信息
                // 一种策略是用新的索引信息覆盖原有索引，这样可以避免文件空洞，但是会失去写入的顺序性
                // 另一种策略是将原有索引覆盖为空白值，这样可以基本保证数据是按时间顺序写入的，但是会额外占用空间
                index = new FileMapIndex(indexChannel.position(), hashCode,
                        dbOriginPosition, valueBytes.length);
            }
            putCache(index, value);
            byte[] indexBytes = index.serialize();

            dbLock = dbChannel.lock(dbChannel.position(), valueBytes.length, false);
            indexLock = indexChannel.lock(indexChannel.position(), FileMapIndex.INDEX_LENGTH, false);
            FileChannelUtils.write(dbChannel, valueBytes);
            FileChannelUtils.write(indexChannel, indexBytes, index.indexPosition);
            // 如果oldCache不为null，说明indexChannel本次执行的是更新操作，更新完成后要将position置到文件末尾
            if (oldCache != null) {
                indexChannel.position(indexChannel.size());
            }
            if (dbLock!=null && dbLock.isValid())        dbLock.release();
            if (indexLock!=null && indexLock.isValid())  indexLock.release();
            if (lock.isWriteLocked() && lock.validate(stamped)) {
                lock.unlockWrite(stamped);
            }

            //根据刷盘策略决定是否执行落盘操作
            if (FileMapOptions.SYNC.equals(syncMode)) {
                dbChannel.force(true);
                indexChannel.force(true);
                if (FileMapOptions.ASYNC.equals(options.getSyncMode()))
                    unwrittenCounter = 0;
            }
            if (FileMapOptions.ASYNC.equals(syncMode)
                && FileMapOptions.ASYNC.equals(options.getSyncMode())) {
                if (++unwrittenCounter >= options.getMaxAsyncCount()) {
                    dbChannel.force(true);
                    indexChannel.force(true);
                    unwrittenCounter = 0;
                }
            }
            return oldCache==null ? null : oldCache.right;
        } catch (IOException | IllegalStateException e) {
            logger.error("FileMap执行put操作时出错！", e);
            return null;
        } finally {
            try {
                if (dbLock!=null && dbLock.isValid())  dbLock.release();
                if (indexLock!=null && indexLock.isValid())  indexLock.release();
                if (lock.isWriteLocked() && lock.validate(stamped))
                    lock.unlockWrite(stamped);
            } catch (IOException e) { e.printStackTrace(); }
        }
    }

    @Override
    public V put(String key, V value) {
        try {
            return put(key, value, options.getSyncMode());
        } catch (JsonProcessingException e) {
            logger.error("FileMap保存值时出错 -- key={}, value={}", key, value, e);
            return null;
        }
    }





    /**
     * 移除指定key：只移除对应key的索引，db中的value将被忽略
     * @param key   要移除的key
     * @return      移除之前key的值
     */
    @Override
    public V remove(String key) {
        waitForInit();
        int hashCode = key.hashCode();
        MutablePair<FileMapIndex, V> pair = getCache(hashCode);
        if (pair == null)   return null;
        FileMapIndex index = pair.left;
        V v = pair.right;
        cache.remove(hashCode);
        FileLock lock = null;
        try {
            lock = indexChannel.lock(indexChannel.position(), Integer.MAX_VALUE, false);
            FileChannelUtils.write(indexChannel, FileMapIndex.BLANK_IDX_BYTES, index.indexPosition);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (lock != null)   lock.release();
            } catch (Exception e) {e.printStackTrace();}
        }
        return v;
    }

    @Override
    public void putAll(Map<String, ? extends V> m) {
        waitForInit();
        if (m.size() > 0) {
            for (Map.Entry<String, ? extends V> e : m.entrySet()) {
                try {
                    put(e.getKey(), e.getValue(), FileMapOptions.ASYNC);
                } catch (JsonProcessingException ex) {
                    logger.error("FileMap putAll error!", ex);
                }
            }
            try {
                indexChannel.force(true);
                dbChannel.force(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private MutablePair<FileMapIndex, V> getCache(Integer keyHashCode) {
        waitForInit();
        return cache.get(keyHashCode);
    }

    /** 用于将文件中读取出来的索引信息重新缓存到内存中 **/
    @SuppressWarnings("unchecked")
    private void putCache(String indexStr, FileChannel channel) {
        byte[] bytes = null;
        try {
            FileMapIndex index = new FileMapIndex(indexStr);
            bytes = FileChannelUtils.read(channel, index.dbStartPosition, index.valLength);
            Map<String, Object> map = JSON.parseObject(bytes, Map.class);
            putCache(index, (V) map.get("value"));
        } catch (Exception e) {
            logger.error("FileMap恢复历史数据时出错，索引信息为 {}， 出错值为 {}",
                        indexStr, new String(bytes==null ? new byte[0] : bytes), e);
        }
    }

    private void putCache(FileMapIndex index, V val) {
        cache.put(index.hash, new MutablePair<>(index, val));
    }

    /**
     * 初始化db时读取磁盘文件中的记录到缓存
     */
    @Override
    public void init() {
        //objMapper = SpringContextUtil.getBean(ObjectMapper.class);
        //objMapper = new ObjectMapper();
        try {
            if (indexChannel.size() <= 0) {
                initialized = true;
                logger.info("FileMap <{}> 初始化完成，共初始化了 0 条原始数据", dbName);
                return;
            }
        } catch (IOException e) {
            logger.error("初始化FileMap过程中出现异常，放弃初始化原始数据！", e);
            return;
        }
        new Thread(() -> {
            long startTime = System.currentTimeMillis();
            try {
                FileChannel rIndexChannel = new RandomAccessFile(
                        dir + dbName + indexSuffix, FileMapOptions.FILE_MODE_R).getChannel();
                FileChannel rDbChannel = new RandomAccessFile(
                        dir + dbName + dbSuffix, FileMapOptions.FILE_MODE_R).getChannel();
                //一次性读出 segmentSize 个完整索引
                int segmentSize = 512 * FileMapIndex.INDEX_LENGTH;
                //初始的索引文件总字节数（单位：byte）
                long originIndexSize = rIndexChannel.size();

                // 每次读取 1024 个完整索引，共包含 1024*FileDbIndex.IDX_MAX_LENGTH 个字符
                long times = rIndexChannel.size() / segmentSize + 1;
                if (rIndexChannel.size() ==  segmentSize * times)
                    times--;

                byte[] blankArray = new byte[FileMapIndex.INDEX_LENGTH];
                for (int i = 0; i < times; i++) {
                    long startPos = (i) * segmentSize;
                    int length = originIndexSize - startPos >= segmentSize ? segmentSize : (int) (originIndexSize - startPos);
                    byte[] bytes = FileChannelUtils.read(rIndexChannel, startPos, length);
                    if (bytes == null || bytes.length < FileMapIndex.INDEX_LENGTH)
                        return;

                    for (int j = 0; j < bytes.length; j = j+FileMapIndex.INDEX_LENGTH) {
                        byte[] indexArray = ArrayUtils.subarray(bytes, j, j + FileMapIndex.INDEX_LENGTH);
                        if (Arrays.equals(blankArray, indexArray)
                                || Arrays.equals(FileMapIndex.BLANK_IDX_BYTES, indexArray))
                            continue;
                        putCache(new String(indexArray), rDbChannel);
                    }
                }
                rDbChannel.close();
                rIndexChannel.close();
            } catch (IOException e) {
                logger.error("FileMap恢复历史数据时出现了未知错误！", e);
            }
            initialized = true;
            logger.info("FileMap <{}> 初始化完成，共初始化了 {} 条原始数据，耗时 {} ms。",
                            dbName, cache.size(), System.currentTimeMillis() - startTime);
        }).start();
    }


}
