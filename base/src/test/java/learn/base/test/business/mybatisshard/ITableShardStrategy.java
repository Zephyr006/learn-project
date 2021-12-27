package learn.base.test.business.mybatisshard;

/**
 * @author Zephyr
 * @date 2021/5/26.
 */
public interface ITableShardStrategy {

    /**
     * @author: 程序猿阿星
     * @description: 生成分表名
     * @param tableNamePrefix 表前缀名
     * @param value 值
     * @date: 2021/5/9
     * @return: java.lang.String
     */
    String generateTableName(String tableNamePrefix,Object value);

    /**
     * 验证tableNamePrefix
     */
    default void verificationTableNamePrefix(String tableNamePrefix){
        if (tableNamePrefix == null || tableNamePrefix.isEmpty() || tableNamePrefix.trim().length() == 0) {
            throw new RuntimeException("TableShard : tableNamePrefix can't be null");
        }
    }

}
