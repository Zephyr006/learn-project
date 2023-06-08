package learn.springboot.datasource.lesson.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import learn.springboot.datasource.lesson.datasource.LessonDataSource;
import learn.springboot.datasource.lesson.entity.MiddlePlatformLessonEntity;
import learn.springboot.extradatasource.registrar.DataServerMapper;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@DataServerMapper(dataSource = LessonDataSource.class)
public interface LessonMapper extends BaseMapper<MiddlePlatformLessonEntity> {


    default List<MiddlePlatformLessonEntity> findByIdInAndStatusTrue(Collection<Long> lessonId) {

        if (CollectionUtils.isEmpty(lessonId)) {
            return Collections.emptyList();
        }
        return selectList(Wrappers.<MiddlePlatformLessonEntity>lambdaQuery()
                .in(MiddlePlatformLessonEntity::getId, lessonId)
                .eq(MiddlePlatformLessonEntity::getStatus, Boolean.TRUE));
    }

    default List<MiddlePlatformLessonEntity> findByRecordVideoIdInAndStatusTrue(Collection<String> videoIdList) {
        if (CollectionUtils.isEmpty(videoIdList)) {
            return Collections.emptyList();
        }
        return selectList(Wrappers.<MiddlePlatformLessonEntity>lambdaQuery()
                .in(MiddlePlatformLessonEntity::getRecordedVideoId, videoIdList)
                .eq(MiddlePlatformLessonEntity::getStatus, Boolean.TRUE));
    }
}
