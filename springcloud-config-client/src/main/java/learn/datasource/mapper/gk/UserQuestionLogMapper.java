package learn.datasource.mapper.gk;

import learn.datasource.datasource.GkDataSource;
import learn.datasource.entity.gk.UserQuestionLog;
import learn.datasource.registrar.DataServerMapper;

import java.util.List;

/**
 * @author: caoyanan
 * @time: 2021/1/14 4:51 下午
 */
@DataServerMapper(dataSource = GkDataSource.class)
public interface UserQuestionLogMapper {

    List<UserQuestionLog> findByDataCenterIdAndCreatedBetween(
            String tableName, Long dataCenterId, Long beginTime, Long endTime);

    List<UserQuestionLog> findByDataCenterIdAndSubmitIdIn(
            String tableName, Long dataCenterId, List<Long> submitIds);
}
