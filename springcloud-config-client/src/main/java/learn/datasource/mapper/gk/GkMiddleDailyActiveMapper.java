package learn.datasource.mapper.gk;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import learn.datasource.datasource.GkDataSource;
import learn.datasource.entity.gk.GkMiddleDailyActive;
import learn.datasource.registrar.DataServerMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Zephyr
 * @date 2021/1/26.
 */
@DataServerMapper(dataSource = GkDataSource.class)
@Repository
public interface GkMiddleDailyActiveMapper extends BaseMapper<GkMiddleDailyActive> {

    // min = 2017-09-21
    @Select("SELECT uid, count(uid) as `count` FROM `middle_daily_active`"
            +" where `active_time` < TIMESTAMPADD( DAY, 30, `user_create_time` ) "
            +" and `uid` in (${uids}) GROUP BY uid")
    List<GkMiddleDailyActive> countUidByUidInAndOneMonthRecently(@Param("uids") String uids);
}
