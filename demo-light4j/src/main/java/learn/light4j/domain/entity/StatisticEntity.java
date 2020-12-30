package learn.light4j.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: caoyanan
 * @time: 2020/12/1 6:57 下午
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StatisticEntity {

    private Long id;

    /**
     * 埋点事件类型
     */
    private Integer eventType;

    /**
     * 时间(0点时间戳 毫秒)
     */
    private Long statisticTime;

    /**
     * 数量
     */
    private Integer count;


    private Long updatedAt;

}
