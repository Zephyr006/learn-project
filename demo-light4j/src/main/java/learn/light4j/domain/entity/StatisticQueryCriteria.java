package learn.light4j.domain.entity;

import lombok.Data;

import java.util.List;

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
