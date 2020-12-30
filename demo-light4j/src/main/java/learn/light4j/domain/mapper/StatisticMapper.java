package learn.light4j.domain.mapper;

import learn.light4j.domain.entity.StatisticEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author: caoyanan
 * @time: 2020/12/7 7:07 下午
 */
public interface StatisticMapper {

    int insertOrUpdate(List<StatisticEntity> statisticCountEntities);

    List<StatisticEntity> findByCriteria(
            @Param("beginTime") Long beginTime,
            @Param("endTime") Long endTime,
            @Param("eventTypes") List<Integer> eventTypes);
}
