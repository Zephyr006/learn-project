package learn.datasource.entity.gk;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author Zephyr
 * @date 2021/1/26.
 */
@Data
@TableName("middle_daily_active")
public class GkMiddleDailyActive {

    private Long uid;
    private Integer count;


}
