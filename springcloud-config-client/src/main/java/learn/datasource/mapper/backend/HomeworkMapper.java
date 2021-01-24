package learn.datasource.mapper.backend;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import learn.datasource.entity.HomeworkEntity;
import learn.datasource.registrar.DataServerMapper;

import java.util.Objects;

/**
 * @author: caoyanan
 * @time: 2021/1/15 3:57 下午
 */
@DataServerMapper
public interface HomeworkMapper extends BaseMapper<HomeworkEntity> {

    default HomeworkEntity findById(Long homeworkId) {
        if (Objects.isNull(homeworkId)) {
            return null;
        }
        return selectOne(Wrappers.<HomeworkEntity>lambdaQuery()
                .eq(HomeworkEntity::getId, homeworkId)
                .eq(HomeworkEntity::getStatus, 0));
    }
}
