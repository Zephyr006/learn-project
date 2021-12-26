package learn.light4j.domain.mapper;

import learn.light4j.domain.entity.StatisticEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface StatisticMapper {

    int insertOrUpdate(List<StatisticEntity> statisticCountEntities);

    List<StatisticEntity> findByCriteria(
            @Param("beginTime") Long beginTime,
            @Param("endTime") Long endTime,
            @Param("eventTypes") List<Integer> eventTypes);
}
