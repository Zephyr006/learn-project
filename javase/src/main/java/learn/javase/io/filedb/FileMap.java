package learn.javase.io.filedb;


import java.io.Serializable;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * 按 java.util.Map 的方式使用即可
 * 对 FileMap 进行数据操作时（读写数据），必须先调用 waitForInit 方法，以保证原始数据已经被初始化
 * @author Zephyr
 * @date 2020/5/25.
 */
public interface FileMap<V extends Serializable> {

    /**
     * 将磁盘中的历史数据初始化到内存中
     */
    void init();
    /**
     * 对应存储通道是否处于打开状态
     * @return true if the channel is open
     */
    boolean isOpen();

    /**
     * 关闭本DB，并释放占用的资源，
     * 关闭后只能通过重新init的方式重新打开
     */
    void close();

    /**
     *
     * @param key
     * @return 返回key所对应的value，如果key不存在，则返回 null
     */
    V get(String key);

    /**
     *
     * @param key
     * @param value
     * @return 上一个与#key关联的值，如果对应的值不存在，则返回 null
     */
    V put(String key, V value);

    /**
     *
     * @param key
     * @return 上一个与#key关联的值，如果对应的值不存在，则返回 null
     */
    V remove(String key);

    /**
     * 判断是否包含指定key
     * @param key
     * @return
     */
    boolean containsKey(String key);


    /**
     * 保存map中的所有键值对
     * @param map
     */
    void putAll(Map<String, ? extends V> map);

    //Set<String> keySet();

    /**
     * 保存的键值对总个数
     * @return
     */
    int size();

    /**
     * 是否为空
     * @return
     */
    boolean isEmpty();

    /**
     * 是否包含指定 value
     * @param value
     * @return
     */
    boolean containsValue(V value);

    //Collection<V> values();

    //Set<Map.Entry<String, V>> entrySet();

    /**
     * 当key存在，并且值为value时，移除该 entry
     * 仅当当前映射到指定值时才删除指定键的条目。
     * @param key
     * @param value
     * @return
     */
    boolean remove(String key, Object value);

    //default void forEach(BiConsumer<String, ? super V> action) {
    //    Objects.requireNonNull(action);
    //    for (Map.Entry<String, V> entry : entrySet()) {
    //        String k;
    //        V v;
    //        try {
    //            k = entry.getKey();
    //            v = entry.getValue();
    //        } catch(IllegalStateException ise) {
    //            // this usually means the entry is no longer in the map.
    //            throw new ConcurrentModificationException(ise);
    //        }
    //        action.accept(k, v);
    //    }
    //}

    /**
     * 如果对应的key不存在，则保存该值，如果已存在，则不执行put操作，并返回原值
     * @param key
     * @param value
     * @return #value
     */
    V putIfAbsent(String key, V value);

    /**
     * 如果指定的键已经不再与值相关联的（或被映射到null ），尝试使用给定的映射函数计算其值并将其输入到该map中，
     * 除非计算结果为null 。
     * 如果函数返回null表示没有对应键值。 如果函数本身抛出（unchecked）异常，异常被重新抛出，不保存映射记录。
     * 最常见的用法是构造一个新的对象作为一个初始值映射 或记忆结果
     * @param key
     * @param mappingFunction
     * @return
     */
    V computeIfAbsent(String key, Function<String, ? extends V> mappingFunction);

    /**
     * 如果对应的key已存在，则应用重映射函数remappingFunction并以计算结果为值更新value
     * 否则返回null
     * @param key
     * @param remappingFunction
     * @return
     */
    V computeIfPresent(String key, BiFunction<String, ? super V, ? extends V> remappingFunction);

    V compute(String key, BiFunction<String, ? super V, ? extends V> remappingFunction);

    /**
     * 当map中存在指定key时，使用当前传入的value替换原来的value
     * 如果不存在该key，则直接返回当前value
     * @param key
     * @param value
     * @return
     */
    V replace(String key, V value);

    boolean replace(String key, V oldValue, V newValue);
}
