package learn.simple.datasource.question.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import learn.simple.datasource.question.datasource.QuestionDataSource;
import learn.simple.datasource.question.entity.Question;
import learn.simple.springboot.extradatasource.registrar.DataServerMapper;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@DataServerMapper(dataSource = QuestionDataSource.class)
public interface QuestionMapper extends BaseMapper<Question> {


    default List<Question> findByIdsAndStatusTrue(Collection<Long> questionIds) {
        if (CollectionUtils.isEmpty(questionIds)) {
            return Collections.emptyList();
        }
        return selectList(Wrappers.<Question>lambdaQuery()
                .in(Question::getId, questionIds)
                .eq(Question::getStatus, Boolean.TRUE));
    }

    default List<Question> findByIdsAndTypeInAndStatusTrue(
            Collection<Long> questionIds, Collection<Integer> types) {
        if (CollectionUtils.isEmpty(questionIds)
                || CollectionUtils.isEmpty(types)) {
            return Collections.emptyList();
        }
        return selectList(Wrappers.<Question>lambdaQuery()
                .in(Question::getId, questionIds)
                .in(Question::getType, types));
    }
}
