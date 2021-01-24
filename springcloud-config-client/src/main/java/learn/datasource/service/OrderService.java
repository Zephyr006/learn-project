package learn.datasource.service;

import learn.datasource.entity.OrderEntity;

import java.util.List;

/**
 * @author: caoyanan
 * @time: 2020/10/29 6:14 下午
 */
public interface OrderService {


    /**
     * 查询订单
     * @param dataCenterIds 用户id
     * @param beginTime 开始时间，毫秒时间戳
     * @param endTime 结束时间，毫秒时间戳
     * @return
     */
    List<OrderEntity> queryOrders(List<Long> dataCenterIds,
                                  Long beginTime, Long endTime);
}
