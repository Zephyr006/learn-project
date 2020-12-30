package learn.light4j.domain.timeperiod;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * @author: caoyanan
 * @time: 2020/11/30 6:25 下午
 */
@Data
@AllArgsConstructor
public class StatisticTimeEvent {

    private String time;

    private List<StatisticEventCount> eventCounts;
}
