package learn.example.javase.io.filedb;

import org.apache.commons.lang3.tuple.MutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author Zephyr
 * @since 2020-06-09.
 */
public abstract class AbstractFileMap<V extends Serializable> implements FileMap<V> {
    private static final Logger logger = LoggerFactory.getLogger(AbstractFileMap.class);
    static final String projectPath = System.getProperty("user.dir") + File.separatorChar;
    static final String dbSuffix = ".db";
    static final String indexSuffix = ".idx";
    //static ObjectMapper objMapper;
    static String dir = projectPath + File.separatorChar + "LocalStorage" + File.separatorChar;

    protected String dbName;
    protected FileMapOptions options;
    protected FileChannel dbChannel;
    protected FileChannel indexChannel;

    /* key: value's hashCode */
    Map<Integer, MutablePair<FileMapIndex, V>> cache = new HashMap<>(512);
    volatile boolean initialized = false;


    @Override
    public boolean containsKey(String key) {
        waitForInit();
        return cache.containsKey(key.hashCode());
    }

    @Override
    public boolean containsValue(V value) {
        waitForInit();
        return cache.values().stream().anyMatch(val -> val.right.equals(value));
    }

    @Override
    public int size() {
        waitForInit();
        return cache.size();
    }

    @Override
    public boolean isEmpty() {
        waitForInit();
        return cache.isEmpty();
    }


    @Override
    public boolean isOpen() {
        return Objects.nonNull(dbChannel) && Objects.nonNull(indexChannel)
                && dbChannel.isOpen() && indexChannel.isOpen();
    }

    @Override
    public void close() {
        try {
            if (Objects.nonNull(dbChannel)) {
                if (dbChannel.isOpen()) {
                    //FileLock lock = dbChannel.lock(dbChannel.position(), Integer.MAX_VALUE, false);
                    dbChannel.force(true);
                    //lock.release();
                }
                dbChannel.close();
            }
            if (Objects.nonNull(indexChannel)) {
                if (indexChannel.isOpen()) {
                    //FileLock lock = indexChannel.lock(indexChannel.position(), Integer.MAX_VALUE, false);
                    indexChannel.force(true);
                    //lock.release();
                }
                indexChannel.close();
            }
            //cache.clear();
            logger.info("FileMap <{}> is closed successfully.", dbName);
        } catch (IOException e) {
            logger.error("FileMap close error!", e);
        }
    }

    @Override
    public boolean remove(String key, Object value) {
        waitForInit();
        Object curValue = get(key);
        if (!Objects.equals(curValue, value) ||
                (curValue == null && !containsKey(key))) {
            return false;
        }
        remove(key);
        return true;
    }

    @Override
    public V putIfAbsent(String key, V value) {
        waitForInit();
        V v = get(key);
        if (v == null) {
            v = put(key, value);
        }
        return v;
    }

    @Override
    public V computeIfAbsent(String key, Function<String, ? extends V> mappingFunction) {
        waitForInit();
        Objects.requireNonNull(mappingFunction);
        V v;
        if ((v = get(key)) == null) {
            V newValue;
            if ((newValue = mappingFunction.apply(key)) != null) {
                put(key, newValue);
                return newValue;
            }
        }
        return v;
    }

    @Override
    public V computeIfPresent(String key, BiFunction<String, ? super V, ? extends V> remappingFunction) {
        waitForInit();
        Objects.requireNonNull(remappingFunction);
        V oldValue;
        if ((oldValue = get(key)) != null) {
            V newValue = remappingFunction.apply(key, oldValue);
            if (newValue != null) {
                put(key, newValue);
                return newValue;
            } else {
                remove(key);
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public V compute(String key, BiFunction<String, ? super V, ? extends V> remappingFunction) {
        waitForInit();
        Objects.requireNonNull(remappingFunction);
        V oldValue = get(key);

        V newValue = remappingFunction.apply(key, oldValue);
        if (newValue == null) {
            // delete mapping
            if (oldValue != null || containsKey(key)) {
                // something to remove
                remove(key);
            }
            return null;
        } else {
            // add or replace old mapping
            put(key, newValue);
            return newValue;
        }
    }

    @Override
    public V replace(String key, V value) {
        waitForInit();
        V curValue;
        if (((curValue = get(key)) != null) || containsKey(key)) {
            curValue = put(key, value);
        }
        return curValue;
    }

    @Override
    public boolean replace(String key, V oldValue, V newValue) {
        waitForInit();
        Object curValue = get(key);
        if (!Objects.equals(curValue, oldValue) ||
                (curValue == null && !containsKey(key))) {
            return false;
        }
        put(key, newValue);
        return true;
    }

    /**
     *  等待程序将磁盘中的旧数据加载到内存
     */
    protected void waitForInit() {
        while (!initialized) {
            try {
                TimeUnit.MILLISECONDS.sleep(10);
            } catch (InterruptedException e) { e.printStackTrace(); }
        }
    }

}
