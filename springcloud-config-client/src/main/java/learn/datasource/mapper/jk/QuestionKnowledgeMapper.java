package learn.datasource.mapper.jk;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import learn.datasource.datasource.JkDataSource;
import learn.datasource.entity.jk.QuestionKnowledge;
import learn.datasource.registrar.DataServerMapper;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author Zephyr
 * @date 2021/1/12.
 */
@DataServerMapper(dataSource = JkDataSource.class)
public interface QuestionKnowledgeMapper extends BaseMapper<QuestionKnowledge> {

    /**
     * 查询题目知识点
     * @param questionIds
     * @return
     */
    default List<QuestionKnowledge> findByQuestionIdInAndStatusTrue(Collection<Long> questionIds) {
        if (CollectionUtils.isEmpty(questionIds)) {
            return Collections.emptyList();
        }
        return selectList(Wrappers.<QuestionKnowledge>lambdaQuery()
                .in(QuestionKnowledge::getQuestionId, questionIds)
                .eq(QuestionKnowledge::getStatus, Boolean.TRUE));
    }
}
