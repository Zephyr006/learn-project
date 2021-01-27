package learn.datasource.client;


import learn.datasource.entity.gk.SubmitLog;

import java.util.List;

/**
 * @author: caoyanan
 * @time: 2021/1/15 6:37 下午
 */
public interface SubmitLogClient {


    /**
     * 查询做题记录
     * @param sceneKey
     * @param sceneId
     * @param dataCenterId
     * @return
     */
    List<SubmitLog> findBySceneKeyAndSceneIdAndDataCenterId(
            Long sceneKey, Long sceneId, Long dataCenterId);
}
