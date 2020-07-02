package learn.javase.io.filedb;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 注意：如果提供的#FileMapOptions设置不同，以FileMap第一次创建时的参数为准
 * @author Zephyr
 * @date 2020/6/4.
 */
public class FileMapFactory {
    static Map<String/*dbName*/, FileMap>
            fileMapCache = new HashMap<>(8);

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <V extends Serializable> FileMap<V> buildSync(String dbName) {
        return FileMapFactory.build(dbName, new FileMapOptions());
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <V extends Serializable> FileMap<V> buildAsync(String dbName) {
        return FileMapFactory.build(dbName, new FileMapOptions().setSyncMode(FileMapOptions.ASYNC));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <V extends Serializable> FileMap<V> build(
                            String dbName, FileMapOptions options) {
        FileMap existedFileMap = fileMapCache.get(dbName);
        if (Objects.nonNull(existedFileMap))
            return existedFileMap;

        FileMapImpl<V> fileMap = new FileMapImpl<>(dbName, options);
        fileMapCache.put(dbName, fileMap);
        return fileMap;
    }


    //private static FileMap putFileMapCache(String dbName, FileMap fileMap) {
    //    FileMap oldFileMap = fileMapCache.get(dbName);
    //    if (Objects.nonNull(oldFileMap))
    //        return oldFileMap;
    //    return fileMapCache.put(dbName, fileMap);
    //}

    public static void destroy() {
        fileMapCache.forEach((dbName, fileMap) -> {
            if (fileMap.isOpen()) {
                fileMap.close();
            }
        });
        fileMapCache.clear();
    }

    public static void init() {
        fileMapCache.forEach((key, fileMap) -> fileMap.init());
    }
}
