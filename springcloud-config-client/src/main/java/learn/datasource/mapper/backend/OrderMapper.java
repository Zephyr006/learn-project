package learn.datasource.mapper.backend;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import learn.datasource.entity.OrderEntity;
import learn.datasource.registrar.DataServerMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @author: caoyanan
 * @time: 2020/10/29 6:22 下午
 */
@DataServerMapper
public interface OrderMapper extends BaseMapper<OrderEntity> {

    List<OrderEntity> findByDataCenterIdInAndPaidTimeBetweenAndStatusIn(
            @Param("dataCenterIds") List<Long> dataCenterIds,
            @Param("beginTime") Date beginTime,
            @Param("endTime") Date endTime,
            @Param("statuses") List<Integer> statuses);
}
