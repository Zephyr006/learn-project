package learn.springcloud.config.client.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import learn.springcloud.config.client.entity.GkUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Zephyr
 * @since 2021-01-11.
 */
@Mapper
@Repository
public interface GkUserMapper extends BaseMapper<GkUser> {
    Class<GkUser> currentModelClass = GkUser.class;

    default List<GkUser> selectPhone(long offset, int limit) {
        LambdaQueryWrapper<GkUser> queryWrapper = Wrappers.<GkUser>lambdaQuery()
                //.ge(User::getId, offset)
                .last("limit " + limit);
        return selectList(queryWrapper);
    }

    default List<GkUser> selectBatchByIdBetween(Long startId, Long endId) {
        return selectList(Wrappers.<GkUser>lambdaQuery()
                .ge(GkUser::getId, startId).lt(GkUser::getId, endId)
                .last("limit " + (endId - startId)));
    }

    default boolean batchInsert(Collection<GkUser> entityList) {
        return batchInsert(entityList, 500);
    }

    default boolean batchInsert(Collection<GkUser> entityList, int batchSize) {

        String sqlStatement = SqlHelper.table(currentModelClass).getSqlStatement(SqlMethod.INSERT_ONE.getMethod());
        //System.out.println(sqlStatement);
        try (SqlSession batchSqlSession = SqlHelper.sqlSessionBatch(currentModelClass)) {
            int i = 0;
            for (GkUser anEntity : entityList) {
                batchSqlSession.insert(sqlStatement, anEntity);
                if (i >= 1 && i % batchSize == 0) {
                    batchSqlSession.flushStatements();
                }
                i++;
            }
            batchSqlSession.flushStatements();
        }
        return true;
    }

    default boolean updateBatchById(Collection<GkUser> entityList, int batchSize) {
        Assert.notEmpty(entityList, "error: entityList must not be empty");

        String sqlStatement = SqlHelper.table(currentModelClass).getSqlStatement(SqlMethod.UPDATE_BY_ID.getMethod());
        try (SqlSession batchSqlSession = SqlHelper.sqlSessionBatch(currentModelClass)) {
            int i = 0;
            for (GkUser anEntityList : entityList) {
                MapperMethod.ParamMap<GkUser> param = new MapperMethod.ParamMap<>();
                param.put(Constants.ENTITY, anEntityList);
                batchSqlSession.update(sqlStatement, param);
                if (i >= 1 && i % batchSize == 0) {
                    batchSqlSession.flushStatements();
                }
                i++;
            }
            batchSqlSession.flushStatements();
        }
        return true;
    }

    default <T> List<T> selectFieldList(SFunction<GkUser, T> selectFieldFunction) {
        List<GkUser> users = selectList(Wrappers.<GkUser>lambdaQuery().select(selectFieldFunction));
        return users.stream().map(selectFieldFunction).collect(Collectors.toList());
    }
}
