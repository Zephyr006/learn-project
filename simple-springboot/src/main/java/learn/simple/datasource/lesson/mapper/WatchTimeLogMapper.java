package learn.simple.datasource.lesson.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import learn.simple.datasource.lesson.datasource.LessonDataSource;
import learn.simple.datasource.lesson.entity.WatchTimeLogEntity;
import learn.simple.springboot.extradatasource.registrar.DataServerMapper;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@DataServerMapper(dataSource = LessonDataSource.class)
public interface WatchTimeLogMapper extends BaseMapper<WatchTimeLogEntity> {


    default List<WatchTimeLogEntity> findByUserIdAndLessonIdInAndStatusTrue(
            Long userId, Collection<Long> lessonId) {

        if (Objects.isNull(userId) || CollectionUtils.isEmpty(lessonId)) {
            return Collections.emptyList();
        }
        return selectList(Wrappers.<WatchTimeLogEntity>lambdaQuery()
                .eq(WatchTimeLogEntity::getUserId, userId)
                .in(WatchTimeLogEntity::getLessonId, lessonId)
                .eq(WatchTimeLogEntity::getStatus, Boolean.TRUE));
    }
}
