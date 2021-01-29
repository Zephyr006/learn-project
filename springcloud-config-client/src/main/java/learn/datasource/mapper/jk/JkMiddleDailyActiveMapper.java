package learn.datasource.mapper.jk;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import learn.datasource.datasource.JkDataSource;
import learn.datasource.entity.jk.JkMiddleDailyActive;
import learn.datasource.registrar.DataServerMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

/**
 * @author Zephyr
 * @date 2021/1/26.
 */
@DataServerMapper(dataSource = JkDataSource.class)
@Repository
public interface JkMiddleDailyActiveMapper extends BaseMapper<JkMiddleDailyActive> {

    // min = 2017-06-14
    @Select("SELECT uid, count(uid) as `count` FROM `middle_daily_active`"
            +" where `active_time` < TIMESTAMPADD( DAY, 30, `user_create_time` ) "
            +" and `uid` in (${uids}) GROUP BY uid")
    List<JkMiddleDailyActive> countUidByUidInAndOneMonthRecently(@Param("uids") String uids);

    // min = 2020-11-23 00:00:00
    @Select("SELECT uid, count(uid) as `count` FROM `middle_daily_active_20201201`"
            +" where `active_time` < TIMESTAMPADD( DAY, 30, `user_create_time` ) "
            +" and `uid` in (${uids}) GROUP BY uid")
    List<JkMiddleDailyActive> count2020UidByUidInAndOneMonthRecently(@Param("uids") String uids);



    @Select("SELECT count(uid) as `count` FROM `middle_daily_active`"
            +" where `active_time` < TIMESTAMPADD( DAY, 30, ' ${createTime} ' ) "
            +" and `uid` = ${uid} ")
    Integer countUidByUidAndCreateTime(@Param("uid") Long uid, @Param("createTime") Timestamp createTime);

    @Select("SELECT count(uid) as `count` FROM `middle_daily_active`"
            +" where `active_time` < TIMESTAMPADD( DAY, 30, ' ${createTime} ' ) "
            +" and `uid` = ${uid} ")
    Integer count2020UidByUidAndCreateTime(@Param("uid") Long uid, @Param("createTime") Timestamp createTime);

}
