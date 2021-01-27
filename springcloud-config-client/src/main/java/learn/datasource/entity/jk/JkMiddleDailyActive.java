package learn.datasource.entity.jk;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author Zephyr
 * @date 2021/1/26.
 */
@Data
@TableName("middle_daily_active")
public class JkMiddleDailyActive {

    private Long uid;
    private Integer count;

}
