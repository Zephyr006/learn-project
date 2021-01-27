package learn.datasource.client;

import learn.datasource.config.ShardingConfig;
import learn.datasource.entity.gk.SubmitLog;
import learn.datasource.mapper.gk.SubmitLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author: caoyanan
 * @time: 2021/1/15 6:41 下午
 */
@Component
public class SubmitLogClientImpl implements SubmitLogClient {

    public final static String TABLE_NAME_TEMPLATE = "submit_log_%s";

    @Autowired
    private SubmitLogMapper submitLogMapper;

    @Autowired
    private ShardingConfig shardingConfig;



    @Override
    public List<SubmitLog> findBySceneKeyAndSceneIdAndDataCenterId(
            Long sceneKey, Long sceneId, Long dataCenterId) {

        if (Objects.isNull(sceneKey)
                || Objects.isNull(sceneId)
                || Objects.isNull(dataCenterId)) {
            return Collections.emptyList();
        }
        return submitLogMapper.findBySceneKeyAndSceneIdAndDataCenterId(
                buildTableName(dataCenterId), sceneKey, sceneId, dataCenterId);
    }

    private String buildTableName(Long userId) {
        return String.format(TABLE_NAME_TEMPLATE,
                userId % shardingConfig.getSubmitLog());
    }
}
