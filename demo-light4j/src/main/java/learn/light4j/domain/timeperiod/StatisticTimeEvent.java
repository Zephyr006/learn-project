package learn.light4j.domain.timeperiod;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;


@Data
@AllArgsConstructor
public class StatisticTimeEvent {

    private String time;

    private List<StatisticEventCount> eventCounts;
}
