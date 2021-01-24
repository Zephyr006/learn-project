package learn.datasource.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author: caoyanan
 * @time: 2020/10/30 5:17 下午
 */
@TableName("daily_active_log")
@Data
public class DailyActiveEntity {

    /**
     * 中台用户id
     */
    private Long dataCenterId;

    /**
     * 登录时间
     */
    private Date activeTime;

    /**
     * 经度
     */
    private Double longitude;

    /**
     * 纬度
     */
    private Double latitude;

    public boolean hasPosition() {
        return longitude != null && latitude != null;
    }
}
