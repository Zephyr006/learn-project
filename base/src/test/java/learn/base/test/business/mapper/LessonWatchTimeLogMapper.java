package learn.base.test.business.mapper;

import learn.base.test.business.entity.LessonWatchTimeLog;
import learn.base.utils.MybatisUtils;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.jdbc.SQL;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

/**
 * @author Zephyr
 * @date 2021/12/3.
 */
@Mapper
public interface LessonWatchTimeLogMapper {
    String tableName = "lesson_watch_time_log";

    @Select("select * from " + tableName + " where id > #{id} limit #{count}")
    List<LessonWatchTimeLog> selectListByIdGt(@Param("id") Long id, @Param("count") int count);

    @UpdateProvider(type = UpdateHelper.class, method = "updateById")
    int updateById(LessonWatchTimeLog entity);

    class UpdateHelper {
        public String updateById(LessonWatchTimeLog entity) {
            Class<?> entityClass = entity.getClass();
            return new SQL() {{
                UPDATE(tableName);
                for (Field field : entityClass.getDeclaredFields()) {
                    Optional.ofNullable(MybatisUtils.parseFieldToUpdateSqlCondition(entity, field, "id"))
                            .ifPresent(this::SET);
                }
                WHERE("id=#{id}");
            }}.toString();
        }
    }
}
