package learn.datasource.client;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import learn.datasource.config.ShardingConfig;
import learn.datasource.entity.relation.UserQuestionLog;
import learn.datasource.mapper.relation.UserQuestionLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author: caoyanan
 * @time: 2021/1/14 5:03 下午
 */
@Component
public class UserQuestionLogClientImpl implements UserQuestionLogClient {


    public static final String TABLE_NAME_TEMPLATE = "user_question_log_%s";

    @Autowired
    private ShardingConfig shardingConfig;

    @Autowired
    private UserQuestionLogMapper userQuestionLogMapper;


    @Override
    public List<UserQuestionLog> findByDataCenterIdAndCreatedBetween(
            Long dataCenterId, Long beginTime, Long endTime) {

        if (Objects.isNull(dataCenterId)) {
            return Collections.emptyList();
        }
        return userQuestionLogMapper.findByDataCenterIdAndCreatedBetween(
                buildTableName(dataCenterId), dataCenterId, beginTime, endTime);
    }

    @Override
    public List<UserQuestionLog> findByDataCenterIdAndSubmitIdIn(Long dataCenterId, List<Long> submitIds) {

        if (Objects.isNull(dataCenterId) || CollectionUtils.isEmpty(submitIds)) {
            return Collections.emptyList();
        }
        return userQuestionLogMapper.findByDataCenterIdAndSubmitIdIn(
                buildTableName(dataCenterId), dataCenterId, submitIds);
    }

    private String buildTableName(Long userId) {
        return String.format(TABLE_NAME_TEMPLATE,
                userId % shardingConfig.getUserQuestionLog());
    }
}
