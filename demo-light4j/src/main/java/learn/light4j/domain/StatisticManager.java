package learn.light4j.domain;

import learn.light4j.domain.entity.StatisticEntity;
import learn.light4j.domain.mapper.StatisticMapper;
import learn.light4j.provider.MysqlStartupHookProvider;
import org.apache.ibatis.session.SqlSession;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class StatisticManager {

    private StatisticManager(){}
    private static final StatisticManager INSTANCE = new StatisticManager();

    public static StatisticManager getInstance() {
        return INSTANCE;
    }

    public Integer batchInsertOrUpdate(List<StatisticEntity> statisticEntities) {
        SqlSession sqlSession = MysqlStartupHookProvider.SQL_SESSION_FACTORY.openSession();
        StatisticMapper mapper = sqlSession.getMapper(StatisticMapper.class);
        int count = mapper.insertOrUpdate(statisticEntities);
        sqlSession.commit();
        sqlSession.close();
        return count;
    }

    public List<StatisticEntity> findByCriteria(
            Long beginTime, Long endTime, List<Integer> eventTypes) {

        if (Objects.isNull(beginTime) || Objects.isNull(endTime)) {
            return Collections.emptyList();
        }
        SqlSession sqlSession = MysqlStartupHookProvider.SQL_SESSION_FACTORY.openSession();
        StatisticMapper mapper = sqlSession.getMapper(StatisticMapper.class);
        List<StatisticEntity> entities = mapper.findByCriteria(beginTime, endTime, eventTypes);
        sqlSession.close();
        return entities;

    }
}
