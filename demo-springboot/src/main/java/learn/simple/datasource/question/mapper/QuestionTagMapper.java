package learn.simple.datasource.question.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import learn.simple.datasource.question.datasource.QuestionDataSource;
import learn.simple.datasource.question.entity.QuestionTag;
import learn.simple.springboot.extradatasource.registrar.DataServerMapper;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author Zephyr
 * @since 2021-01-12.
 */
@DataServerMapper(dataSource = QuestionDataSource.class)
public interface QuestionTagMapper extends BaseMapper<QuestionTag> {

    /**
     * 查询题目知识点
     * @param questionIds
     * @return
     */
    default List<QuestionTag> findByQuestionIdInAndStatusTrue(Collection<Long> questionIds) {
        if (CollectionUtils.isEmpty(questionIds)) {
            return Collections.emptyList();
        }
        return selectList(Wrappers.<QuestionTag>lambdaQuery()
                .in(QuestionTag::getQuestionId, questionIds)
                .eq(QuestionTag::getStatus, Boolean.TRUE));
    }
}
