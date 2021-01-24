package learn.datasource.service;

import learn.datasource.entity.DailyActiveEntity;
import learn.datasource.entity.UserLoginInfo;

import java.util.List;

/**
 * @author: caoyanan
 * @time: 2020/10/30 5:25 下午
 */
public interface DailyActiveService {

    /**
     * 查询登录信息
     * @param dataCenterId 中台id
     * @param beginTime 开始时间(毫秒时间戳，包含)
     * @param endTime 结束时间(毫秒时间戳，不包含)
     * @return
     */
    List<DailyActiveEntity> queryDailyActive(
            List<Long> dataCenterId, Long beginTime, Long endTime);

    List<UserLoginInfo> queryUserLoginInfo(List<Long> dataCenterIds);
}
