package learn.datasource.mapper.gk;

import learn.datasource.datasource.GkDataSource;
import learn.datasource.entity.gk.SubmitLog;
import learn.datasource.registrar.DataServerMapper;

import java.util.List;

/**
 * @author: caoyanan
 * @time: 2021/1/15 6:43 下午
 */
@DataServerMapper(dataSource = GkDataSource.class)
public interface SubmitLogMapper {


    List<SubmitLog> findBySceneKeyAndSceneIdAndDataCenterId(
            String tableName, Long sceneKey,
            Long sceneId, Long dataCenterId);
}
