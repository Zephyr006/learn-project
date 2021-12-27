package learn.light4j.domain.timeperiod;

import lombok.AllArgsConstructor;
import lombok.Data;

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
