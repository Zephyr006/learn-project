package learn.datasource.client;

import learn.datasource.entity.gk.UserQuestionLog;

import java.util.List;

/**
 * @author: caoyanan
 * @time: 2021/1/14 5:01 下午
 */
public interface UserQuestionLogClient {

    /**
     * 查询学员一段时间内的做题记录
     * @param dataCenterId
     * @param beginTime
     * @param endTime
     * @return
     */
    List<UserQuestionLog> findByDataCenterIdAndCreatedBetween(Long dataCenterId, Long beginTime, Long endTime);


    /**
     * 批量查询学员某次提交zuodetimu
     * @param dataCenterId
     * @param submitIds
     */
    List<UserQuestionLog> findByDataCenterIdAndSubmitIdIn(Long dataCenterId, List<Long> submitIds);
}
