package learn.light4j.domain.entity;

import lombok.Data;

import java.util.List;

/**
 * @author: caoyanan
 * @time: 2020/12/1 2:22 下午
 */
@Data
public class StatisticQueryCriteria {

    /**
     * 开始时间
     */
    private Long startTime;

    /**
     * 结束时间
     */
    private Long endTime;

    /**
     * 埋点类型
     */
    private List<Integer> eventTypes;

    /**
     * 时间周期
     */
    private Integer timePeriod;
}
