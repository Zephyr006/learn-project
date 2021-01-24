package learn.datasource.mapper.question;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import learn.datasource.datasource.QuestionDataSource;
import learn.datasource.entity.question.QuestionDetail;
import learn.datasource.registrar.DataServerMapper;

import java.util.Collections;
import java.util.List;

/**
 * @author: caoyanan
 * @time: 2021/1/18 2:16 下午
 */
@DataServerMapper(dataSource = QuestionDataSource.class)
public interface QuestionDetailMapper extends BaseMapper<QuestionDetail> {


    default List<QuestionDetail> findByQuestionIdsAndStatusTrue(List<Long> questionIds) {
        if (CollectionUtils.isEmpty(questionIds)) {
            return Collections.emptyList();
        }
        return selectList(Wrappers.<QuestionDetail>lambdaQuery()
                .in(QuestionDetail::getQuestionId, questionIds)
                .eq(QuestionDetail::getStatus, Boolean.TRUE));
    }
}
