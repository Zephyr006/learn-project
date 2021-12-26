package learn.base.test.business.mybatisshard;

/**
 * @author Zephyr
 * @date 2021/5/26.
 */
public class IdTableShardStrategy implements ITableShardStrategy {

    @Override
    public String generateTableName(String tableNamePrefix, Object value) {
        return "9";
    }
}
