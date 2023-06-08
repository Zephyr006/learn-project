package learn.springboot.datasource.question.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import learn.springboot.datasource.question.datasource.QuestionDataSource;
import learn.springboot.datasource.question.entity.QuestionKnowledge;
import learn.springboot.extradatasource.registrar.DataServerMapper;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author Zephyr
 * @since 2021-01-12.
 */
@DataServerMapper(dataSource = QuestionDataSource.class)
public interface QuestionKnowledgeMapper extends BaseMapper<QuestionKnowledge> {

    /**
     * 查询题目知识点
     * @param questionIds
     * @return
     */
    default List<QuestionKnowledge> findByQuestionIdInAndStatusTrue(
            Collection<Long> questionIds) {
        if (CollectionUtils.isEmpty(questionIds)) {
            return Collections.emptyList();
        }
        return selectList(Wrappers.<QuestionKnowledge>lambdaQuery()
                .in(QuestionKnowledge::getQuestionId, questionIds)
                .eq(QuestionKnowledge::getStatus, Boolean.TRUE));
    }
}
