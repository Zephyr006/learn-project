package learn.datasource.mapper.relation;

import learn.datasource.datasource.RelationDataSource;
import learn.datasource.entity.relation.SubmitLog;
import learn.datasource.registrar.DataServerMapper;

import java.util.List;

/**
 * @author: caoyanan
 * @time: 2021/1/15 6:43 下午
 */
@DataServerMapper(dataSource = RelationDataSource.class)
public interface SubmitLogMapper {


    List<SubmitLog> findBySceneKeyAndSceneIdAndDataCenterId(
            String tableName, Long sceneKey,
            Long sceneId, Long dataCenterId);
}
