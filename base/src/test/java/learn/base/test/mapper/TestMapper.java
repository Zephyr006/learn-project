package learn.base.test.mapper;

import learn.base.test.mybatisshard.IdTableShardStrategy;
import learn.base.test.mybatisshard.TableShard;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author Zephyr
 * @date 2021/5/26.
 */
public interface TestMapper {

    @TableShard(tableNamePrefix = "app_user_group", shardStrategy = IdTableShardStrategy.class)
    @Select("select * from app_user_group where status = ${status}")
    List<Object> selectAll(Integer id, @Param("status") boolean status);
}
