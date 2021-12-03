package learn.base.test.mapper;

import learn.base.test.entity.LessonWatchTimeLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author Zephyr
 * @date 2021/12/3.
 */
@Mapper
public interface LessonWatchTimeLogMapper {

    @Select("select * from lesson_watch_time_log where id > #{id} limit #{count}")
    List<LessonWatchTimeLog> selectListByIdGt(@Param("id") Long id, @Param("count") int count);

}
