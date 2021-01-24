package learn.datasource.client;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import learn.datasource.config.ShardingConfig;
import learn.datasource.entity.relation.UserQuestion;
import learn.datasource.mapper.relation.UserQuestionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author: caoyanan
 * @time: 2021/1/18 1:48 下午
 */
@Component
public class UserQuestionClientImpl implements UserQuestionClient {

    public static final String TABLE_NAME_TEMPLATE = "user_question_%s";


    @Autowired
    private ShardingConfig shardingConfig;

    @Autowired
    private UserQuestionMapper userQuestionMapper;


    @Override
    public List<UserQuestion> findByDataCenterIdAndQuestionIdIn(Long dataCenterId, List<Long> questionIds) {

        if (Objects.isNull(dataCenterId) || CollectionUtils.isEmpty(questionIds)) {
            return Collections.emptyList();
        }
        return userQuestionMapper.findByDataCenterIdAndQuestionIdIn(
                buildTableName(dataCenterId), dataCenterId, questionIds);
    }


    private String buildTableName(Long userId) {
        return String.format(TABLE_NAME_TEMPLATE,
                userId % shardingConfig.getUserQuestion());
    }
}
