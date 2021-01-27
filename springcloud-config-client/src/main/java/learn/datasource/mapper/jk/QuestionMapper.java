package learn.datasource.mapper.jk;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import learn.datasource.datasource.JkDataSource;
import learn.datasource.entity.jk.Question;
import learn.datasource.registrar.DataServerMapper;

import java.util.Collections;
import java.util.List;

/**
 * @author: caoyanan
 * @time: 2021/1/18 2:16 下午
 */
@DataServerMapper(dataSource = JkDataSource.class)
public interface QuestionMapper extends BaseMapper<Question> {


    default List<Question> findByIdsAndStatusTrue(List<Long> questionIds) {
        if (CollectionUtils.isEmpty(questionIds)) {
            return Collections.emptyList();
        }
        return selectList(Wrappers.<Question>lambdaQuery()
                .in(Question::getId, questionIds)
                .eq(Question::getStatus, Boolean.TRUE));
    }

    default List<Question> findByIdsAndTypeInAndStatusTrue(
            List<Long> questionIds, List<Integer> types) {
        if (CollectionUtils.isEmpty(questionIds)
                || CollectionUtils.isEmpty(types)) {
            return Collections.emptyList();
        }
        return selectList(Wrappers.<Question>lambdaQuery()
                .in(Question::getId, questionIds)
                .in(Question::getType, types));
    }
}
