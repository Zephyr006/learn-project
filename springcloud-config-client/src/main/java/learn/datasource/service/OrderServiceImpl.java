package learn.datasource.service;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import learn.datasource.constant.OrderStatus;
import learn.datasource.entity.OrderEntity;
import learn.datasource.mapper.backend.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author: caoyanan
 * @time: 2020/10/29 6:22 下午
 */
@Service
public class OrderServiceImpl implements OrderService{

    @Autowired
    private OrderMapper orderMapper;

    @Override
    public List<OrderEntity> queryOrders(List<Long> dataCenterIds, Long beginTime, Long endTime) {

        if (CollectionUtils.isEmpty(dataCenterIds)
                && Objects.isNull(beginTime)
                && Objects.isNull(endTime)) {
            return Collections.emptyList();
        }
        Date beginTimeDate = null;
        if (Objects.nonNull(beginTime)) {
            beginTimeDate = new Date(beginTime);
        }
        Date endTimeDate = null;
        if (Objects.nonNull(endTime)) {
            endTimeDate = new Date(endTime);
        }
        List<Integer> status = Arrays.asList(OrderStatus.PAYED.getValue(), OrderStatus.REFUND.getValue());
        return orderMapper.findByDataCenterIdInAndPaidTimeBetweenAndStatusIn(
                dataCenterIds, beginTimeDate, endTimeDate, status);
    }
}
