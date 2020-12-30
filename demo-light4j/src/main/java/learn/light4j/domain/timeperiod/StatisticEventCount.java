package learn.light4j.domain.timeperiod;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author: caoyanan
 * @time: 2020/11/30 6:23 下午
 */
@Data
@AllArgsConstructor
public class StatisticEventCount {

    /**
     * 埋点事件
     */
    private String event;

    /**
     * 统计数量
     */
    private Integer count;

}
