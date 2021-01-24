package learn.datasource.mapper.backend;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import learn.datasource.entity.DailyActiveEntity;
import learn.datasource.model.UserLoginCountModel;
import learn.datasource.registrar.DataServerMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @author: caoyanan
 * @time: 2020/10/30 5:21 下午
 */
@DataServerMapper
public interface DailyActiveMapper extends BaseMapper<DailyActiveEntity> {

    List<DailyActiveEntity> findByDataCenterIdInAndActiveTimeBetween(
            @Param("dataCenterIds") List<Long> dataCenterId,
            @Param("beginTime") Date beginTime,
            @Param("endTime") Date endTime);

    List<UserLoginCountModel> countByUserIds(@Param("userIdList") List<Long> userIdList);
}
